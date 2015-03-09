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

import java.lang.reflect.Array;
import java.util.Iterator;

import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.segment.IterationMeta;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * <code>
 * {{#each items}}
 *  {{name}}
 * {{/each}}
 * </code>
 *
 * <p>
 * It's possible to apply a function to each element. The function must be an
 * instance of {@link Function}. Note that the function cannot be type-safe. If
 * the result does not equal to {@link EachHelper#SKIP_RESULT} it's used instead
 * of the original element. If the result equals to
 * {@link EachHelper#SKIP_RESULT} the element is skipped. This might be useful
 * to filter out unnecessary elements or to wrap/transform elements.
 * </p>
 *
 * <code>
 * {{#each items apply=myFunction}}
 *  {{name}}
 * {{/each}}
 * </code>
 * <p>
 *
 * @see Function
 * @author Martin Kouba
 */
public class EachHelper extends BasicSectionHelper {

    public static final String SKIP_RESULT = "org.trimou.handlebars.skipResult";

    private String iterationMetadataAlias;

    @Override
    public void init() {
        super.init();
        this.iterationMetadataAlias = configuration
                .getStringPropertyValue(EngineConfigurationKey.ITERATION_METADATA_ALIAS);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Options options) {

        Object value = options.getParameters().get(0);

        if (value instanceof Iterable) {
            processIterable((Iterable) value, options);
        } else if (value.getClass().isArray()) {
            processArray(value, options);
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "%s is nor an Iterable nor an array [%s]", value,
                    options.getTagInfo());
        }
    }

    @SuppressWarnings("rawtypes")
    private void processIterable(Iterable iterable, Options options) {

        Iterator iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        final Function function = initFunction(options);
        final IterationMeta meta = new IterationMeta(iterationMetadataAlias,
                iterator);

        options.push(meta);
        while (iterator.hasNext()) {
            processNextElement(options, iterator.next(), meta, function);
        }
        options.pop();
    }

    private void processArray(Object array, Options options) {

        int length = Array.getLength(array);
        if (length < 1) {
            return;
        }
        final Function function = initFunction(options);
        final IterationMeta meta = new IterationMeta(iterationMetadataAlias,
                length);

        options.push(meta);
        for (int i = 0; i < length; i++) {
            processNextElement(options, Array.get(array, i), meta, function);
        }
        options.pop();
    }

    private void processNextElement(Options options, Object value,
            IterationMeta meta, Function function) {
        if (function != null) {
            Object result = function.apply(value);
            if (!SKIP_RESULT.equals(result)) {
                next(options, result, meta);
            }
        } else {
            next(options, value, meta);
        }
    }

    private void next(Options options, Object value, IterationMeta meta) {
        options.push(value);
        options.fn();
        options.pop();
        meta.nextIteration();
    }

    private Function initFunction(Options options) {
        Object function = getHashValue(options, APPLY);
        if (function == null) {
            return null;
        }
        if (function instanceof Function) {
            return (Function) function;
        }
        throw new MustacheException(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                "%s is not a valid function [%s]", function,
                options.getTagInfo());
    }

}
