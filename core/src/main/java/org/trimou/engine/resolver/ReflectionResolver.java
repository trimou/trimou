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
package org.trimou.engine.resolver;

import static org.trimou.engine.priority.Priorities.after;
import static org.trimou.util.Checker.checkArgumentNotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Reflections;

/**
 * Reflection-based resolver attempts to find a matching member on the context
 * object class and its superclasses. Methods have higher priority than fields.
 *
 * @author Martin Kouba
 * @see Reflections#findField(Class, String)
 * @see Reflections#findMethod(Class, String)
 */
public class ReflectionResolver extends AbstractResolver {

    public static final int REFLECTION_RESOLVER_PRIORITY = after(ListIndexResolver.LIST_RESOLVER_PRIORITY, 3);

    public static final String COMPUTING_CACHE_CONSUMER_ID = ReflectionResolver.class
            .getName();

    /**
     * Limit the size of the cache (e.g. to avoid problems when dynamic class
     * compilation is involved). Use zero value to disable the cache.
     */
    public static final ConfigurationKey MEMBER_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
            ReflectionResolver.class.getName() + ".memberCacheMaxSize", 10000L);

    /**
     * Even if the runtime class of the context object changes try to apply the
     * resolver.
     */
    public static final ConfigurationKey HINT_FALLBACK_ENABLED_KEY = new SimpleConfigurationKey(
            ReflectionResolver.class.getName() + ".hintFallbackEnabled", true);

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ReflectionResolver.class);

    private static final MemberWrapper ARRAY_GET_LENGTH = Array::getLength;

    public static final String GET_PREFIX = "get";
    public static final String IS_PREFIX = "is";

    /**
     * Lazy loading cache of lookup attempts (contains both hits and misses)
     */
    private ComputingCache<MemberKey, Optional<MemberWrapper>> memberCache;

    private boolean hintFallbackEnabled;

    public ReflectionResolver() {
        this(REFLECTION_RESOLVER_PRIORITY);
    }

    public ReflectionResolver(int priority) {
        super(priority);
    }

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null) {
            return null;
        }

        MemberWrapper wrapper;
        MemberKey key = MemberKey.newInstance(contextObject, name);
        if (memberCache != null) {
            wrapper = memberCache.get(key).orElse(null);
        } else {
            wrapper = findWrapper(key).orElse(null);
        }

        if (wrapper == null) {
            return null;
        }

        try {
            return wrapper.getValue(contextObject);
        } catch (Exception e) {
            throw new MustacheException(
                    MustacheProblem.RENDER_REFLECT_INVOCATION_ERROR, e);
        }
    }

    @Override
    public Hint createHint(Object contextObject, String name,
            ResolutionContext context) {
        MemberKey key = MemberKey.newInstance(contextObject, name);
        MemberWrapper wrapper;
        if (memberCache != null) {
            Optional<MemberWrapper> found = memberCache.getIfPresent(key);
            wrapper = found != null ? found.get() : null;
        } else {
            wrapper = findWrapper(key).orElse(null);
        }
        if (wrapper != null) {
            return new ReflectionHint(key, wrapper);
        }
        // This should never happen
        return Hints.INAPPLICABLE_HINT;
    }

    @Override
    public void init() {
        long memberCacheMaxSize = configuration
                .getLongPropertyValue(MEMBER_CACHE_MAX_SIZE_KEY);
        LOGGER.debug("Initialized [memberCacheMaxSize: {}]",
                memberCacheMaxSize);
        if (memberCacheMaxSize > 0) {
            memberCache = configuration.getComputingCacheFactory().create(
                    COMPUTING_CACHE_CONSUMER_ID, ReflectionResolver::findWrapper,
                    null, memberCacheMaxSize, null);
        }
        hintFallbackEnabled = configuration
                .getBooleanPropertyValue(HINT_FALLBACK_ENABLED_KEY);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(MEMBER_CACHE_MAX_SIZE_KEY);
    }

    /**
     * The member cache may theoretically cause memory leaks due to using hard
     * references to {@link Class} and {@link Member} instances. As a temporary
     * workaround we provide a way to invalidate the cache or some of its
     * entries (e.g. for a concrete classloader).
     *
     * @param predicate
     *            If null, all cache entries are discarded, otherwise an entry
     *            is only discarded if the given predicate returns
     *            <code>true</code> for the {@link MemberKey#getClass()}
     */
    public void invalidateMemberCache(Predicate<Class<?>> predicate) {
        if (memberCache == null) {
            return;
        }
        if (predicate == null) {
            memberCache.clear();
        } else {
            memberCache.invalidate((key) -> predicate.test(key.getClazz()));
        }
    }

    long getMemberCacheSize() {
        return memberCache != null ? memberCache.size() : 0L;
    }

    private static Optional<MemberWrapper> findWrapper(MemberKey key) {
        // Get length of array objects
        if (key.getClazz().isArray()) {
            if (key.getName().equals("length")) {
                return Optional.of(ARRAY_GET_LENGTH);
            } else {
                return Optional.empty();
            }
        }

        // Find accesible method with the given name, no
        // parameters and non-void return type
        Method foundMethod = findMethod(key.getClazz(), key.getName());

        if (foundMethod != null) {
            if (!foundMethod.isAccessible()) {
                SecurityActions.setAccessible(foundMethod);
            }
            return Optional.of(new MethodWrapper(foundMethod));
        }

        // Find public field
        Field foundField = findField(key.getClazz(), key.getName());

        if (foundField != null) {
            if (!foundField.isAccessible()) {
                SecurityActions.setAccessible(foundField);
            }
            return Optional.of(new FieldWrapper(foundField));
        }
        // Member not found
        return Optional.empty();
    }

    private class ReflectionHint implements Hint {

        private final MemberKey key;

        private final MemberWrapper wrapper;

        /**
         *
         * @param key
         * @param wrapper
         */
        ReflectionHint(MemberKey key, MemberWrapper wrapper) {
            this.key = key;
            this.wrapper = wrapper;
        }

        @Override
        public Object resolve(Object contextObject, String name,
                ResolutionContext context) {
            if (contextObject == null) {
                return null;
            }
            if (key.getClazz().equals(contextObject.getClass())) {
                try {
                    return wrapper.getValue(contextObject);
                } catch (Exception e) {
                    return null;
                }
            }
            // The runtime class of the context object changed
            if (ReflectionResolver.this.hintFallbackEnabled) {
                return ReflectionResolver.this.resolve(contextObject, name,
                        context);
            }
            return null;
        }
    }

    /**
     * First tries to find a valid method with the same name, afterwards method
     * following JavaBean naming convention (the method starts with
     * <b>get/is</b> prefix).
     *
     * @param clazz
     * @param name
     * @return the found method or <code>null</code>
     */
    static Method findMethod(Class<?> clazz, String name) {

        checkArgumentNotNull(clazz);
        checkArgumentNotNull(name);

        Method foundMatch = null;
        Method foundGetMatch = null;
        Method foundIsMatch = null;

        for (Method method : SecurityActions.getMethods(clazz)) {

            if (!isMethodValid(method)) {
                continue;
            }

            if (method.isBridge()) {
                LOGGER.debug("Skipping bridge method {}", method);
                continue;
            }

            if (name.equals(method.getName())) {
                foundMatch = method;
            } else if (Reflections.matchesPrefix(name, method.getName(),
                    GET_PREFIX)) {
                foundGetMatch = method;
            } else if (Reflections.matchesPrefix(name, method.getName(),
                    IS_PREFIX)) {
                foundIsMatch = method;
            }
        }

        if (foundMatch == null) {
            foundMatch = (foundGetMatch != null ? foundGetMatch : foundIsMatch);
        }

        LOGGER.debug("{} method {}found [type: {}]", name, foundMatch != null ? "" : "not ", clazz.getName());
        return foundMatch;
    }

    /**
     * Tries to find a public field with the given name on the given class.
     *
     * @param clazz
     * @param name
     * @return the found field or <code>null</code>
     */
    static Field findField(Class<?> clazz, String name) {

        checkArgumentNotNull(clazz);
        checkArgumentNotNull(name);

        Field found = null;

        for (Field field : SecurityActions.getFields(clazz)) {
            if (field.getName().equals(name)) {
                found = field;
            }
        }
        LOGGER.debug("{} field {}found [type: {}]", name, found != null ? "" : "not ", clazz.getName());
        return found;
    }

    /**
     * A valid method:
     * <ul>
     * <li>is public</li>
     * <li>has no parameters</li>
     * <li>has non-void return type</li>
     * <li>its declaring class is not {@link Object}</li>
     * </ul>
     *
     * @param method
     * @return <code>true</code> if the given method is considered a read method
     */
    private static boolean isMethodValid(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 0
                && !method.getReturnType().equals(Void.TYPE)
                && !Object.class.equals(method.getDeclaringClass());
    }

}
