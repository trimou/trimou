/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.handlebars;

import static org.trimou.handlebars.OptionsHashKeys.APPLY;
import static org.trimou.handlebars.OptionsHashKeys.AS;
import static org.trimou.handlebars.OptionsHashKeys.OMIT_META;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Stream;

import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.segment.ImmutableIterationMeta;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableSet;
import org.trimou.util.Iterables;

/**
 * Iterates over the params or the object at the top of the context stack.
 *
 * <p>
 * The param could be {@link Iterable}, array, {@link Iterator},
 * {@link Spliterator} and {@link Stream}. If any of the params is
 * {@link Iterator}, {@link Spliterator} or {@link Stream} the iteration
 * metadata are not provided. Null values are treated as empty objects.
 * </p>
 *
 * <pre>
 * {{#each items}}
 *  {{name}}
 * {{/each}}
 * </pre>
 *
 * <p>
 * It's possible to apply a function to each element. The function must be an
 * instance of {@link Function} or a string referencing a built-in function. If
 * the result does not equal to {@link EachHelper#SKIP_RESULT} it's used instead
 * of the original element. If the result equals to
 * {@link EachHelper#SKIP_RESULT} the element is skipped. This might be useful
 * to filter out unnecessary elements or to wrap/transform elements.
 * </p>
 *
 * <pre>
 * {{#each items apply=myFunction}}
 *  {{name}}
 * {{/each}}
 * </pre>
 *
 * <p>
 * There are some built-in functions that can be specified using string
 * literals:
 * </p>
 * <ul>
 * <li>{@value #SKIP_IF_NULL} - skip all null elements</li>
 * <li>{@value #SKIP_IF} - skip an element if the result of an expression is
 * "truthy" (see also {@link Checker#isFalsy(Object)})</li>
 * <li>{@value #SKIP_UNLESS} - skip an element if the result of an expression is
 * "falsy"</li>
 * <li>{@value #MAP} - replace the element with the result of an expression</li>
 * </ul>
 *
 * <pre>
 * {{#each items apply="skipUnless:active"}}
 *  Inactive items are skipped
 * {{/each}}
 *
 * {{#each items apply="skipIf:name.isEmpty"}}
 *  Items with null or empty names are skipped
 * {{/each}}
 *
 * {{#each items apply="map:name"}}
 *  Iterate over names
 * {{/each}}
 * </pre>
 *
 * <p>
 * It's also possible to supply an alias to access the value of the current
 * iteration:
 * </p>
 *
 * <pre>
 * {{#each items as='item'}}{{item.name}}{{/each}}
 * </pre>
 *
 * <p>
 * This helper could be used to iterate over multiple objects:
 * <p>
 *
 * <pre>
 * {{! First iterate over list1 and then iterate over list2}}
 * {{#each list1 list2}}
 *  {{name}}
 * {{/each}}
 * </pre>
 *
 * <p>
 * By default, the parameters are analyzed and iteration metadata are available.
 * In some cases, it could be useful to omit the analysis and iteration metadata
 * generation:
 * </p>
 *
 * <pre>
 * {{#each items omitMeta=true}}{{this}}{{/each}}
 * </pre>
 *
 * @see Function
 * @author Martin Kouba
 */
public class EachHelper extends BasicSectionHelper {

    public static final String SKIP_RESULT = "org.trimou.handlebars.skipResult";

    private static final String SKIP_IF_NULL = "skipIfNull";

    private static final String SKIP_UNLESS = "skipUnless:";

    private static final String SKIP_IF = "skipIf:";

    private static final String MAP = "map:";

    private static final Function SKIP_NULL_FUNC = (e) -> e != null ? e : SKIP_RESULT;

    private String iterationMetadataAlias;

    private ReflectionResolver reflectionResolver;

    @Override
    public void init() {
        super.init();
        this.iterationMetadataAlias = configuration
                .getStringPropertyValue(EngineConfigurationKey.ITERATION_METADATA_ALIAS);
    }

    @Override
    public void execute(Options options) {
        if (options.getParameters().isEmpty()) {
            // No params - try the object at the top of the context stack
            Object head = options.peek();
            processParameter(head, options, 1, getSize(head), isOmitMeta(options));
        } else if (options.getParameters().size() == 1) {
            // Single param
            Object param = options.getParameters().get(0);
            if (param == null) {
                // Treat null values as empty objects
                return;
            }
            processParameter(param, options, 1, getSize(param), isOmitMeta(options));
        } else {
            // Multiple params require some additional handling
            int size = 0;
            int index = 1;
            boolean omitMeta = isOmitMeta(options);
            List<Object> params = new ArrayList<>(options.getParameters());
            for (Iterator<Object> iterator = params.iterator(); iterator.hasNext();) {
                Object param = iterator.next();
                if (param == null) {
                    // Treat null values as empty objects
                    iterator.remove();
                    continue;
                }
                if (param instanceof Iterator || param instanceof Spliterator || param instanceof Stream) {
                    omitMeta = true;
                }
            }
            if (!omitMeta) {
                for (Iterator<Object> iterator = params.iterator(); iterator.hasNext();) {
                    Object param = iterator.next();
                    int paramSize = 0;
                    if (param != null) {
                        paramSize = getSize(param);
                    }
                    if (paramSize > 0) {
                        size += paramSize;
                    } else {
                        iterator.remove();
                    }
                }
            }
            if (!omitMeta && size == 0) {
                return;
            }
            for (Object param : params) {
                index = processParameter(param, options, index, size, omitMeta);
            }
        }
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    @Override
    protected Set<String> getSupportedHashKeys() {
        return ImmutableSet.of(APPLY, AS, OMIT_META);
    }

    private int processParameter(Object param, Options options, int index, int size, boolean isOmitMeta) {
        if (param instanceof Iterable) {
            return processIterator(((Iterable<?>) param).iterator(), options, index, size, isOmitMeta);
        } else if (param.getClass().isArray()) {
            return processArray(param, options, index, size, isOmitMeta);
        } else if (param instanceof Iterator) {
            return processIterator((Iterator<?>) param, options, index, size, true);
        } else if (param instanceof Spliterator) {
            return processSpliterator((Spliterator<?>) param, options, index, size, true);
        } else if (param instanceof Stream) {
            return processSpliterator(((Stream<?>) param).sequential().spliterator(), options, index, size, true);
        } else {
            throw new MustacheException(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "%s is nor an Iterable nor an array [%s]", param, options.getTagInfo());
        }
    }

    private int processIterator(Iterator<?> iterator, Options options, int index, int size, boolean isOmitMeta) {
        Function function = initFunction(options);
        String alias = initValueAlias(options);
        while (iterator.hasNext()) {
            nextElement(options, iterator.next(), size, index++, function, alias, isOmitMeta);
        }
        return index;
    }

    private int processArray(Object array, Options options, int index, int size, boolean isOmitMeta) {
        int length = Array.getLength(array);
        Function function = initFunction(options);
        String alias = initValueAlias(options);
        for (int i = 0; i < length; i++) {
            nextElement(options, Array.get(array, i), size, index++, function, alias, isOmitMeta);
        }
        return index;
    }

    private int processSpliterator(Spliterator<?> spliterator, Options options, int index, int size,
            boolean isOmitMeta) {
        Function function = initFunction(options);
        String alias = initValueAlias(options);
        spliterator.forEachRemaining((e) -> {
            nextElement(options, e, size, Integer.MIN_VALUE, function, alias, isOmitMeta);
        });
        return Integer.MIN_VALUE;
    }

    private int getSize(Object param) {
        if (param instanceof Iterable) {
            return Iterables.size((Iterable<?>) param);
        } else if (param.getClass().isArray()) {
            return Array.getLength(param);
        }
        return 0;
    }

    private void nextElement(Options options, Object value, int size, int index, Function function, String valueAlias,
            boolean isOmitMeta) {
        if (function != null) {
            value = function.apply(value);
            if (SKIP_RESULT.equals(value)) {
                return;
            }
        }
        ImmutableIterationMeta meta = isOmitMeta ? null
                : (valueAlias != null
                        ? new ImmutableIterationMeta(iterationMetadataAlias, size, index, valueAlias, value)
                        : new ImmutableIterationMeta(iterationMetadataAlias, size, index));
        if (meta != null) {
            options.push(meta);
        }
        options.push(value);
        options.fn();
        options.pop();
        if (meta != null) {
            options.pop();
        }
    }

    private Function initFunction(Options options) {
        Object function = options.getHash().get(APPLY);
        if (function == null) {
            return null;
        }
        if (function instanceof Function) {
            return (Function) function;
        }
        String functionStr = function.toString();
        if (SKIP_IF_NULL.equals(functionStr)) {
            return SKIP_NULL_FUNC;
        } else if (functionStr.startsWith(SKIP_IF)) {
            return skip(functionStr, options, false);
        } else if (function.toString().startsWith(SKIP_UNLESS)) {
            return skip(functionStr, options, true);
        } else if (function.toString().startsWith(MAP)) {
            ReflectionResolver resolver = getReflectionResolver(functionStr, options);
            return (e) -> resolve(resolver, e, functionStr.substring(MAP.length()));
        }
        throw new MustacheException(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS, "%s is not a valid function [%s]",
                function, options.getTagInfo());
    }

    private Function skip(String funcStr, Options options, boolean unless) {
        ReflectionResolver resolver = getReflectionResolver(funcStr, options);
        if (unless) {
            return e -> Checker.isFalsy(resolve(resolver, e, funcStr.substring(SKIP_UNLESS.length()))) ? SKIP_RESULT
                    : e;
        } else {
            return e -> !Checker.isFalsy(resolve(resolver, e, funcStr.substring(SKIP_IF.length()))) ? SKIP_RESULT : e;
        }
    }

    private Object resolve(ReflectionResolver resolver, Object element, String key) {
        Object value = element;
        for (Iterator<String> iterator = configuration.getKeySplitter().split(key); iterator.hasNext();) {
            String keyPart = iterator.next();
            Object resolved = resolver.resolve(value, keyPart, null);
            if (resolved == null) {
                // Value not found - miss
                return null;
            } else {
                value = resolved;
            }
        }
        return value;
    }

    private ReflectionResolver getReflectionResolver(String functionStr, Options options) {
        if (reflectionResolver == null) {
            // Synchronization is not needed
            for (Resolver resolver : configuration.getResolvers()) {
                if (resolver instanceof ReflectionResolver) {
                    reflectionResolver = (ReflectionResolver) resolver;
                    break;
                }
            }
        }
        if (reflectionResolver == null) {
            throw new MustacheException(MustacheProblem.RENDER_GENERIC_ERROR,
                    "%s cannot be used - ReflectionResolver not found [%s]", functionStr, options.getTagInfo());
        }
        return reflectionResolver;
    }

    private String initValueAlias(Options options) {
        Object as = options.getHash().get(AS);
        if (as == null) {
            return null;
        }
        return as.toString();
    }

    private boolean isOmitMeta(Options options) {
        Object value = options.getHash().get(OMIT_META);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return Boolean.TRUE.equals(value);
        }
        return Boolean.parseBoolean(value.toString());
    }

}
