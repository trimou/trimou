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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.trimou.engine.resolver.Decorator;
import org.trimou.engine.resolver.Decorator.AbstractBuilder;
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
 * By default, the helper throws {@link IllegalStateException} if the runtime
 * class of the context object is not assignable to the delegate type. A custom
 * predicate can be used but beware of generics pitfalls and
 * {@link ClassCastException}s.
 * </p>
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
        return new Builder<>(o -> delegateType.isAssignableFrom(o.getClass()));
    }

    /**
     * Returns a decorator helper builder with the specified predicate used to test
     * a context object.
     *
     * @param delegateType
     * @return a new builder instance
     */
    public static <T> Builder<T> decorate(Predicate<Object> test) {
        return new Builder<>(test);
    }

    private final String delegateKey;

    private final Predicate<Object> test;

    private final Map<String, Function<T, Object>> mappings;

    private DecoratorHelper(Predicate<Object> test, Map<String, Function<T, Object>> mappings, String delegateKey) {
        this.test = test;
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
        if (!test.test(param)) {
            throw new IllegalStateException(
                    "Param " + param.getClass() + " is not applicable: " + options.getTagInfo());
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

    public static class Builder<T> extends AbstractBuilder<T, Builder<T>> {

        private final Predicate<Object> test;

        private Builder(Predicate<Object> test) {
            Checker.checkArgumentNotNull(test);
            this.test = test;
        }

        @Override
        protected Builder<T> self() {
            return this;
        }

        /**
         *
         * @return a new decorator helper instance
         */
        public DecoratorHelper<T> build() {
            return new DecoratorHelper<T>(test, ImmutableMap.copyOf(mappings), delegateKey);
        }

    }

}
