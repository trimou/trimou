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
package org.trimou.engine.resolver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.config.Configuration;
import org.trimou.exception.MustacheException;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableMap;

/**
 * This util class can be used to decorate a delegate instance, e.g. to add a
 * computed property to a context object (data) passed to
 * {@link Mustache#render(Object)}. The following snippet would render: "ooF".
 *
 * <pre>
 * MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
 * engine.compileMustache("{{reverse}}")
 *         .render(decorate("Foo").compute("reverse", s -> new StringBuilder(s).reverse().toString()).build(engine));
 * </pre>
 *
 * <p>
 * Note that {@link ReflectionResolver} and {@link MapResolver} must be
 * registered in order to make it work.
 * </p>
 *
 * @author Martin Kouba
 * @see Builder
 */
public class Decorator<T> implements Mapper {

    public static final String KEY_GET_DELEGATE = "delegate";

    /**
     * Returns a decorator builder for the specified delegate.
     *
     * @param delegate
     * @return a new builder instance
     * @see Builder#build(MustacheEngine)
     */
    public static <T> Builder<T> decorate(T delegate) {
        return new Builder<T>(delegate);
    }

    /**
     * Returns a decorator instance for the specified delegate and mappings.
     *
     * @param delegate
     * @param mappings
     * @param delegateKey
     *            May be null
     * @param configuration
     * @return a new decorator instance
     */
    public static <T> Decorator<T> decorate(T delegate, Map<String, Function<T, Object>> mappings, String delegateKey,
            Configuration configuration) {
        return IterableDecorator.isIterable(delegate)
                ? new IterableDecorator<>(delegate, ImmutableMap.copyOf(mappings), delegateKey, configuration)
                : new Decorator<>(delegate, ImmutableMap.copyOf(mappings), delegateKey, configuration);
    }

    /**
     * This method is recursive.
     *
     * @return the underlying delegate instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(T instance) {
        return instance instanceof Decorator ? unwrap(((Decorator<T>) instance).delegate) : instance;
    }

    private final String delegateKey;

    protected final T delegate;

    private final ReflectionResolver reflectionResolver;

    private final Map<String, Function<T, Object>> mappings;

    /**
     *
     * @param delegate
     * @param mappings
     * @param configuration
     */
    private Decorator(T delegate, Map<String, Function<T, Object>> mappings, String delegateKey,
            Configuration configuration) {
        this.delegate = delegate;
        this.reflectionResolver = getReflectionResolver(configuration);
        this.mappings = mappings;
        this.delegateKey = delegateKey != null ? delegateKey : KEY_GET_DELEGATE;
    }

    @Override
    public Object get(String key) {
        if (delegateKey.equals(key)) {
            return delegate;
        } else {
            Function<T, Object> mapping = mappings.get(key);
            if (mapping != null) {
                return mapping.apply(unwrap(delegate));
            } else if (delegate instanceof Mapper) {
                return ((Mapper) delegate).get(key);
            } else {
                return reflectionResolver.resolve(delegate, key, null);
            }
        }
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    private ReflectionResolver getReflectionResolver(Configuration configuration) {
        for (Resolver resolver : configuration.getResolvers()) {
            if (resolver instanceof ReflectionResolver) {
                return (ReflectionResolver) resolver;
            }
        }
        throw new IllegalStateException("Decorator can only be used if ReflectionResolver is available");
    }

    public static class Builder<T> extends AbstractBuilder<T, Builder<T>> {

        private final T delegate;

        private Builder(T delegate) {
            super();
            Checker.checkArgumentNotNull(delegate);
            this.delegate = delegate;
        }

        @Override
        protected Builder<T> self() {
            return this;
        }

        /**
         *
         * @param engine
         * @return a new decorator instance
         * @throws IllegalStateException
         *             If {@link ReflectionResolver} is not available
         */
        public Decorator<T> build(MustacheEngine engine) {
            return build(engine.getConfiguration());
        }

        /**
         *
         * @param engine
         * @return a new decorator instance
         * @throws IllegalStateException
         *             If {@link ReflectionResolver} is not available
         */
        public Decorator<T> build(Configuration configuration) {
            Checker.checkArgumentNotNull(configuration);
            return Decorator.decorate(delegate, ImmutableMap.copyOf(mappings), delegateKey, configuration);
        }

    }

    /**
     * This version is used for delegates that are arrays or implement an iterable
     * type.
     *
     * @param <T>
     */
    private static class IterableDecorator<T> extends Decorator<T> implements Iterable<Object> {

        static boolean isIterable(Object delegate) {
            return delegate instanceof Iterable || delegate.getClass().isArray() || delegate instanceof Iterator
                    || delegate instanceof Spliterator || delegate instanceof Stream;
        }

        private IterableDecorator(T delegate, Map<String, Function<T, Object>> mappings, String delegateKey,
                Configuration configuration) {
            super(delegate, mappings, delegateKey, configuration);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Iterator<Object> iterator() {
            if (delegate instanceof Iterable) {
                return ((Iterable) delegate).iterator();
            } else if (delegate.getClass().isArray()) {
                // This is not very effective
                int length = Array.getLength(delegate);
                List<Object> elements = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    elements.add(Array.get(delegate, i));
                }
                return elements.iterator();
            } else if (delegate instanceof Iterator) {
                return (Iterator) delegate;
            } else if (delegate instanceof Spliterator) {
                return Spliterators.iterator((Spliterator<?>) delegate);
            } else if (delegate instanceof Stream) {
                return ((Stream) delegate).sequential().iterator();
            } else {
                throw new MustacheException(delegate + "is not iterable");
            }
        }

    }

    /**
     * Logic shared accross various decorator-related builders.
     *
     * @param <T>
     * @param <B>
     */
    public abstract static class AbstractBuilder<T, B extends AbstractBuilder<T, B>> {

        protected String delegateKey;

        protected final Map<String, Function<T, Object>> mappings;

        public AbstractBuilder() {
            this.mappings = new HashMap<>();
        }

        /**
         * Associates the specified value with the specified key.
         *
         * @param key
         * @param value
         * @return self
         */
        public B put(String key, Object value) {
            return compute(key, delegate -> value);
        }

        /**
         * Associates the specified mapping function with the specified key. The input
         * to the function is the delegate instance. The function is applied each time a
         * value for the given key is requested.
         *
         * @param key
         * @param mapper
         * @return self
         */
        public B compute(String key, Function<T, Object> mapper) {
            Checker.checkArgumentsNotNull(key, mapper);
            mappings.put(key, mapper);
            return self();
        }
        
        /**
         * Associates the specified mapping function with the specified key. The input
         * to the function is the delegate instance. The function is only applied once -
         * when a value for the given key is requested the result is cached.
         *
         * @param key
         * @param mapper
         * @return self
         */
        public B computeOnce(String key, Function<T, Object> mapper) {
            Checker.checkArgumentsNotNull(key, mapper);
            mappings.put(key, new Function<T, Object>() {

                private volatile Object value;

                @Override
                public Object apply(T delegate) {
                    Object ret = value;
                    if (ret == null) {
                        synchronized (this) {
                            ret = value;
                            if (ret == null) {
                                ret = mapper.apply(delegate);
                                value = ret;
                            }
                        }
                    }
                    return ret;
                }
            });
            return self();
        }

        /**
         * The specified key can be used to obtain the undelying delegate instance.
         *
         * @param key
         * @return self
         * @see Decorator#KEY_GET_DELEGATE
         */
        public B delegateKey(String key) {
            this.delegateKey = key;
            return self();
        }

        protected abstract B self();

    }

}
