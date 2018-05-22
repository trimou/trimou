/*
 * Copyright 2018 Trimou team
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.trimou.engine.resolver.Decorator;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableMap;

/**
 * This section helper allows to decorate the first param or the object at the
 * top of the context stack, e.g. to add a computed property. The following
 * snippet would render: "ooF".
 *
 * <pre>
 * MustacheEngine engine = MustacheEngineBuilder.newBuilder()
 *         .registerHelper("decorateStr",
 *                 decorate(String.class).compute("reverse", s -> new StringBuilder(s).reverse().toString()).build())
 *         .build();
 * engine.compileMustache("{{#decorateStr}}{{reverse}}{{/decorateStr}}").render("Foo");
 * </pre>
 *
 * <p>
 * Note that {@link ReflectionResolver} and {@link MapResolver} must be
 * registered in order to make it work.
 * </p>
 *
 * @author Martin Kouba
 */
public class DecoratorHelper<T> extends BasicSectionHelper {

    /**
     * Returns a decorator helper builder for the specified delegate type.
     *
     * @param delegateType
     * @return a new builder instance
     */
    public static <T> Builder<T> decorate(Class<T> delegateType) {
        return new Builder<>(delegateType);
    }

    private final String delegateKey;

    private final Class<T> delegateType;

    private final Map<String, Function<T, Object>> mappings;

    private DecoratorHelper(Class<T> type, Map<String, Function<T, Object>> mappings, String delegateKey) {
        this.delegateType = type;
        this.mappings = mappings;
        this.delegateKey = delegateKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Options options) {
        Object param = options.getParameters().size() == 1 ? options.getParameters().get(0) : options.peek();
        if (param == null) {
            // Treat null values as empty objects
            return;
        }
        if (!delegateType.isAssignableFrom(param.getClass())) {
            throw new IllegalStateException("Param " + param.getClass() + " is not assignable from " + delegateType);
        }
        options.push(Decorator.decorate((T) param, mappings, delegateKey != null ? delegateKey.toString() : null,
                configuration));
        options.fn();
        options.pop();
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    public static class Builder<T> {

        private String delegateKey;

        private final Class<T> delegateType;

        private final Map<String, Function<T, Object>> mappings;

        private Builder(Class<T> type) {
            Checker.checkArgumentNotNull(type);
            this.delegateType = type;
            this.mappings = new HashMap<>();
        }

        /**
         * Associates the specified value with the specified key.
         *
         * @param key
         * @param value
         * @return self
         */
        public Builder<T> put(String key, Object value) {
            return compute(key, (o) -> value);
        }

        /**
         * Associates the specified mapping function with the specified key. The input
         * to the function is the current delegate instance. The function is applied
         * each time a value for the given key is requested.
         *
         * @param key
         * @param mapper
         * @return self
         */
        public Builder<T> compute(String key, Function<T, Object> mapper) {
            Checker.checkArgumentsNotNull(key, mapper);
            mappings.put(key, mapper);
            return this;
        }

        /**
         * The specified key can be used to obtain the undelying delegate instance.
         *
         * @param key
         * @return self
         */
        public Builder<T> delegateKey(String key) {
            this.delegateKey = key;
            return this;
        }

        /**
         *
         * @return a new decorator helper instance
         */
        DecoratorHelper<T> build() {
            return new DecoratorHelper<T>(delegateType, ImmutableMap.copyOf(mappings), delegateKey);
        }

    }

}
