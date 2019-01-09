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
package org.trimou.engine.convert;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.engine.resolver.Decorator;
import org.trimou.engine.resolver.Decorator.AbstractBuilder;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableMap;

/**
 * This converter allows you to decorate a context object, e.g. to add a computed
 * property. The following snippet would render: "ooF".
 *
 * <pre>
 * MustacheEngine engine = MustacheEngineBuilder.newBuilder()
 *         .addContextConverter(
 *                 decorate(String.class).compute("reverse", s -> new StringBuilder(s).reverse().toString()).build())
 *         .build();
 * engine.compileMustache("{{reverse}}").render("Foo");
 * </pre>
 *
 * <p>
 * By default, the converter is applied if the runtime class of the context
 * object is assignable to the delegate type. A custom predicate can be used but
 * beware of generics pitfalls and {@link ClassCastException}s.
 * </p>
 *
 * <p>
 * Note that {@link ReflectionResolver} and {@link MapResolver} must be
 * registered in order to make it work.
 * </p>
 *
 * @author Martin Kouba
 */
public class DecoratorConverter<T> extends AbstractConfigurationAware implements ContextConverter {

    /**
     * Returns a decorator converter builder for the specified delegate type.
     *
     * @param delegateType
     * @return a new builder instance
     */
    public static <T> Builder<T> decorate(Class<T> delegateType) {
        return decorate(o -> (delegateType.isAssignableFrom(Decorator.unwrap(o).getClass())));
    }

    /**
     * Returns a decorator converter builder with the specified predicate used to
     * test a context object.
     *
     * @param delegateType
     * @return a new builder instance
     */
    public static <T> Builder<T> decorate(Predicate<Object> test) {
        return new Builder<>(test);
    }

    private final Predicate<Object> test;

    private final String delegateKey;

    private final Map<String, Function<T, Object>> mappings;

    private DecoratorConverter(Predicate<Object> test, String delegateKey, Map<String, Function<T, Object>> mappings) {
        this.test = test;
        this.delegateKey = delegateKey;
        this.mappings = mappings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object from) {
        if (test.test(from)) {
            return Decorator.decorate((T) from, mappings, delegateKey != null ? delegateKey.toString() : null,
                    configuration);
        }
        return null;
    }

    public static class Builder<T> extends AbstractBuilder<T, Builder<T>> {

        private final Predicate<Object> test;

        private Builder(Predicate<Object> test) {
            super();
            Checker.checkArgumentNotNull(test);
            this.test = test;
        }

        @Override
        protected Builder<T> self() {
            return this;
        }

        /**
         *
         * @return a new decorator converter instance
         */
        public DecoratorConverter<T> build() {
            return new DecoratorConverter<T>(test, delegateKey, ImmutableMap.copyOf(mappings));
        }

    }

}
