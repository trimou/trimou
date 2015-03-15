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

import static org.trimou.engine.priority.Priorities.rightBefore;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.priority.WithPriority;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Reflections;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Reflection-based resolver attempts to find a matching member on the context
 * object class and its superclasses. Methods have higher priority than fields.
 *
 * @author Martin Kouba
 * @see Reflections#findField(Class, String)
 * @see Reflections#findMethod(Class, String)
 */
public class ReflectionResolver extends AbstractResolver implements
        RemovalListener<MemberKey, Optional<MemberWrapper>> {

    private static final Logger logger = LoggerFactory
            .getLogger(ReflectionResolver.class);

    public static final int REFLECTION_RESOLVER_PRIORITY = rightBefore(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

    public static final String COMPUTING_CACHE_CONSUMER_ID = ReflectionResolver.class
            .getName();

    public ReflectionResolver() {
        this(REFLECTION_RESOLVER_PRIORITY);
    }

    public ReflectionResolver(int priority) {
        super(priority);
    }

    /**
     * Limit the size of the cache (e.g. to avoid problems when dynamic class
     * compilation is involved). Use zero value to disable the cache.
     */
    public static final ConfigurationKey MEMBER_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
            ReflectionResolver.class.getName() + ".memberCacheMaxSize", 10000l);

    /**
     * Lazy loading cache of lookup attempts (contains both hits and misses)
     */
    private ComputingCache<MemberKey, Optional<MemberWrapper>> memberCache;

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null) {
            return null;
        }

        MemberWrapper wrapper;
        MemberKey key = MemberKey.newInstance(contextObject, name);
        if (memberCache != null) {
            wrapper = memberCache.get(key).orNull();
        } else {
            wrapper = findWrapper(key).orNull();
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
    public Hint createHint(Object contextObject, String name) {
        MemberKey key = MemberKey.newInstance(contextObject, name);
        MemberWrapper wrapper;
        if (memberCache != null) {
            Optional<MemberWrapper> found = memberCache.getIfPresent(key);
            wrapper = found != null ? found.get() : null;
        } else {
            wrapper = findWrapper(key).orNull();
        }
        if (wrapper != null) {
            return new ReflectionHint(key, wrapper);
        }
        return null;
    }

    @Override
    public void init() {
        long memberCacheMaxSize = configuration
                .getLongPropertyValue(MEMBER_CACHE_MAX_SIZE_KEY);
        logger.info("Initialized [memberCacheMaxSize: {}]", memberCacheMaxSize);
        if (memberCacheMaxSize > 0) {
            memberCache = configuration.getComputingCacheFactory().create(
                    COMPUTING_CACHE_CONSUMER_ID, new MemberComputingFunction(),
                    null, memberCacheMaxSize, null);
        }
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections
                .<ConfigurationKey> singleton(MEMBER_CACHE_MAX_SIZE_KEY);
    }

    @Override
    public void onRemoval(
            RemovalNotification<MemberKey, Optional<MemberWrapper>> notification) {
        // This is not used anymore, should be removed in the next major version
        // (together with RemovalListener)
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
    public void invalidateMemberCache(final Predicate<Class<?>> predicate) {
        if (memberCache == null) {
            return;
        }
        if (predicate == null) {
            memberCache.clear();
        } else {
            memberCache
                    .invalidate(new ComputingCache.KeyPredicate<MemberKey>() {
                        @Override
                        public boolean apply(MemberKey key) {
                            return predicate.apply(key.getClazz());
                        }
                    });
        }
    }

    long getMemberCacheSize() {
        return memberCache != null ? memberCache.size() : 0l;
    }

    private static Optional<MemberWrapper> findWrapper(MemberKey key) {
        // Find accesible method with the given name, no
        // parameters and non-void return type
        Method foundMethod = Reflections.findMethod(key.getClazz(),
                key.getName());

        if (foundMethod != null) {
            return Optional.<MemberWrapper> of(new MethodWrapper(foundMethod));
        }

        // Find public field
        Field foundField = Reflections.findField(key.getClazz(), key.getName());

        if (foundField != null) {
            return Optional.<MemberWrapper> of(new FieldWrapper(foundField));
        }
        // Member not found
        return Optional.absent();
    }

    private static class MemberComputingFunction implements
            ComputingCache.Function<MemberKey, Optional<MemberWrapper>> {

        @Override
        public Optional<MemberWrapper> compute(MemberKey key) {
            return findWrapper(key);
        }

    }

    private static class ReflectionHint implements Hint {

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
        public Object resolve(Object contextObject, String name) {
            if (contextObject == null
                    || !key.getClazz().equals(contextObject.getClass())) {
                // No context object or the runtime class of the context object
                // changed
                return null;
            }
            try {
                return wrapper.getValue(contextObject);
            } catch (Exception e) {
                return null;
            }
        }
    }

}
