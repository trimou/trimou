/*
 * Copyright 2015 Martin Kouba
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

import static org.trimou.handlebars.OptionsHashKeys.CLASS;
import static org.trimou.handlebars.OptionsHashKeys.M;
import static org.trimou.handlebars.OptionsHashKeys.METHOD;
import static org.trimou.handlebars.OptionsHashKeys.ON;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Primitives;

/**
 * Invokes public methods with parameters via reflection.
 *
 * <p>
 * All the helper parameters are considered to be method parameters. The method
 * name must be always defined - either using the key {@link OptionsHashKeys#M}
 * or {@link OptionsHashKeys#METHOD}. The instance specified by
 * {@value OptionsHashKeys#ON} key is optional - if not specified and not a
 * static method invocation, the object at the top of the context stack is used.
 * The key {@link OptionsHashKeys#CLASS} may be used to invoke a static method
 * of a specific class. By default, the TCCL or the CL of this helper is used to
 * load the class if needed.
 * </p>
 *
 * <p>
 * E.g. the following template will invoke
 * {@link String#replace(CharSequence, CharSequence)} method on {@code "foo"}
 * string with paramteres {@code "f"} and {@code "b"}.
 * </p>
 *
 * <pre>
 * {{invoke "f" "b" on="foo" m="replace"}}
 * </pre>
 *
 * <p>
 * In the next example, {@link String#split(String)} is invoked, the resulting
 * array is pushed on the context stack, we iterate over the array and render
 * values converted to upper case letters.
 * <p>
 *
 * <pre>
 * {{#invoke ":" on="foo:bar" m="split"}}{{#each this}}{{toUpperCase}}{{/each}}{{/invoke}}
 * </pre>
 *
 * <p>
 * It's also possible to invoke a static method:
 * </p>
 *
 * <pre>
 * {{#invoke 'MILLISECONDS' class='java.util.concurrent.TimeUnit' m='valueOf'}}{{invoke 1000L m='toSeconds'}}{{/invoke}}
 * </pre>
 *
 * <p>
 * It might be also useful to access the values of a map with non-string keys:
 * </p>
 *
 * <pre>
 * {{invoke myNonStringKey on=myMap m="get"}}
 * </pre>
 *
 * <p>
 * If no instance is specified and not a static method invocation, the object at
 * the top of the context stack is used:
 * </p>
 *
 * <pre>
 * {{#with item.name}}{{invoke 1 m='substring'}}{{/with}}
 * </pre>
 *
 *
 * @author Martin Kouba
 */
public class InvokeHelper extends BasicHelper {

    /**
     * Limit the size of the cache. Use zero value to disable the cache.
     */
    public static final ConfigurationKey METHOD_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
            InvokeHelper.class.getName() + ".methodCacheMaxSize", 500l);

    private ComputingCache<MethodKey, Optional<Method>> methodCache;

    private final ClassLoader classLoader;

    /**
     *
     */
    public InvokeHelper() {
        ClassLoader cl = SecurityActions.getContextClassLoader();
        if (cl == null) {
            cl = SecurityActions.getClassLoader(InvokeHelper.class);
        }
        this.classLoader = cl;
    }

    /**
     *
     * @param classLoader
     *            The CL used to load a class for a static method invocation
     */
    public InvokeHelper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void execute(Options options) {

        Class<?> clazz = null;
        Object methodName = getHashValue(options, M);
        if (methodName == null) {
            methodName = getHashValue(options, METHOD);
        }
        Object instance = getHashValue(options, ON);
        if (instance == null) {
            clazz = loadClassIfNeeded(options);
            if (clazz == null) {
                instance = options.peek();
            }
        }
        if (clazz == null) {
            clazz = instance.getClass();
        }

        Method method = methodCache.get(new MethodKey(clazz,
                methodName.toString(), getParamTypes(options))).orNull();
        if (method == null) {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "Unable to find unambiguous method with name \"%s\" and parameter types %s on class %s [%s]",
                    methodName, getParamTypes(options), clazz.getName(),
                    options.getTagInfo());
        }

        try {
            Object value = method.invoke(instance,
                    options.getParameters().toArray());
            if (isSection(options)) {
                if (value != null) {
                    options.push(value);
                    options.fn();
                    options.pop();
                }
            } else {
                if (value == null) {
                    value = configuration.getMissingValueHandler()
                            .handle(options.getTagInfo());
                }
                if (value != null) {
                    append(options, value.toString());
                }
            }
        } catch (Exception e) {
            throw new MustacheException(MustacheProblem.RENDER_GENERIC_ERROR,
                    e);
        }
    }

    @Override
    public void init() {
        super.init();
        this.methodCache = configuration.getComputingCacheFactory()
                .create(InvokeHelper.class
                        .getName(), new MethodComputingFunction(), null,
                configuration.getLongPropertyValue(METHOD_CACHE_MAX_SIZE_KEY),
                null);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return super.getConfigurationKeys();
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    @Override
    public void validate(HelperDefinition definition) {
        super.validate(definition);
        if (!definition.getHash().containsKey(METHOD)
                && !definition.getHash().containsKey(M)) {
            throw HelperValidator.newValidationException(
                    "A method name must be always defined", this.getClass(),
                    definition);
        }
    }

    @Override
    protected Optional<Set<String>> getSupportedHashKeys() {
        return Optional.<Set<String>> of(ImmutableSet.of(ON, M, METHOD, CLASS));
    }

    private static boolean matches(Method method, List<Class<?>> paramTypes) {
        Class<?>[] methodParamTypes = method.getParameterTypes();
        if (methodParamTypes.length != paramTypes.size()) {
            return false;
        }
        for (int i = 0; i < methodParamTypes.length; i++) {
            Class<?> type = Primitives.wrap(methodParamTypes[i]);
            if (!type.isAssignableFrom(paramTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

    private List<Class<?>> getParamTypes(Options options) {
        int size = options.getParameters().size();
        if (size == 0) {
            return Collections.emptyList();
        }
        List<Class<?>> paramTypes = new ArrayList<>(size);
        for (Object param : options.getParameters()) {
            paramTypes.add(param.getClass());
        }
        return paramTypes;
    }

    private Class<?> loadClassIfNeeded(Options options) {
        Class<?> clazz = null;
        try {
            Object clazzValue = getHashValue(options, CLASS);
            if (clazzValue != null) {
                if (clazzValue instanceof Class<?>) {
                    clazz = (Class<?>) clazzValue;
                } else {
                    clazz = classLoader.loadClass(clazzValue.toString());
                }
            }
        } catch (ClassNotFoundException ignored) {
        }
        return clazz;
    }

    private static final class MethodKey {

        private final Class<?> clazz;

        private final String name;

        private final List<Class<?>> paramTypes;

        private MethodKey(Class<?> clazz, String name,
                List<Class<?>> paramTypes) {
            this.clazz = clazz;
            this.name = name;
            this.paramTypes = paramTypes;
        }

        Class<?> getClazz() {
            return clazz;
        }

        String getName() {
            return name;
        }

        List<Class<?>> getParamTypes() {
            return paramTypes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result
                    + ((paramTypes == null) ? 0 : paramTypes.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MethodKey other = (MethodKey) obj;
            if (clazz == null) {
                if (other.clazz != null) {
                    return false;
                }
            } else if (!clazz.equals(other.clazz)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (paramTypes == null) {
                if (other.paramTypes != null) {
                    return false;
                }
            } else if (!paramTypes.equals(other.paramTypes)) {
                return false;
            }
            return true;
        }

    }

    private static class MethodComputingFunction
            implements ComputingCache.Function<MethodKey, Optional<Method>> {

        @Override
        public Optional<Method> compute(MethodKey key) {
            List<Method> found = findMethods(key.getClazz(), key.getName());
            if (found.isEmpty()) {
                return Optional.absent();
            }
            for (Iterator<Method> iterator = found.iterator(); iterator
                    .hasNext();) {
                Method method = iterator.next();
                if (!matches(method, key.getParamTypes())) {
                    iterator.remove();
                }
            }
            if (found.size() == 1) {
                Method method = found.get(0);
                if ((!Modifier.isPublic(method.getModifiers()) || !Modifier
                        .isPublic(method.getDeclaringClass().getModifiers()))
                        && !method.isAccessible()) {
                    SecurityActions.setAccessible(method);
                }
                return Optional.of(method);
            }
            return Optional.absent();
        }

    }

    /**
     *
     * @param clazz
     * @param name
     * @return the list of public methods defined on the specified class and
     *         having the specified name
     */
    private static List<Method> findMethods(Class<?> clazz, String name) {
        List<Method> found = new ArrayList<>();
        for (Method method : SecurityActions.getMethods(clazz)) {
            if (name.equals(method.getName())) {
                found.add(method);
            }
        }
        return found;
    }

}
