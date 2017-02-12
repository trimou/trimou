/*
 * Copyright 2016 Martin Kouba
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
package org.trimou.engine.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.cache.ComputingCache.Listener;
import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableMap.ImmutableMapBuilder;

/**
 * A default computing cache factory producing computing cache implementations
 * backed by {@link ConcurrentHashMap}.
 *
 * @author Martin Kouba
 * @see Map#computeIfAbsent(Object, java.util.function.Function)
 */
public class DefaultComputingCacheFactory extends AbstractConfigurationAware
        implements ComputingCacheFactory {

    private final MaxSizeStrategy maxSizeStrategy;

    /**
     *
     * @see MaxSizeStrategy#CLEAR
     */
    public DefaultComputingCacheFactory() {
        this(MaxSizeStrategy.CLEAR);
    }

    /**
     *
     * @param maxSizeStrategy
     */
    public DefaultComputingCacheFactory(MaxSizeStrategy maxSizeStrategy) {
        Checker.checkArgumentNotNull(maxSizeStrategy);
        this.maxSizeStrategy = maxSizeStrategy;
    }

    @Override
    public <K, V> ComputingCache<K, V> create(String consumerId,
            Function<K, V> computingFunction, Long expirationTimeout,
            Long maxSize, Listener<K> listener) {
        if (maxSize != null && !maxSizeStrategy.isEvictionSupported()) {
            throw new IllegalArgumentException(
                    "Max size limit not supported - use a different eviction strategy");
        }
        return new ConcurrentHashMapAdapter<>(computingFunction, maxSize,
                maxSizeStrategy, expirationTimeout, listener);
    }

    /**
     *
     * @author Martin Kouba
     *
     * @param <K>
     * @param <V>
     */
    private static class ConcurrentHashMapAdapter<K, V>
            implements ComputingCache<K, V> {

        private static final Logger LOGGER = LoggerFactory
                .getLogger(ConcurrentHashMapAdapter.class);

        private final MaxSizeStrategy maxSizeStrategy;

        private final Long maxSize;

        private final Long expirationTimeout;

        private final Listener<K> listener;

        private final ConcurrentHashMap<K, CacheEntry<V>> map;

        private final java.util.function.Function<K, CacheEntry<V>> computingFunctionAdapter;

        /**
         *
         * @param computingFunction
         * @param maxSize
         * @param maxSizeStrategy
         */
        ConcurrentHashMapAdapter(
                ComputingCache.Function<K, V> computingFunction, Long maxSize,
                MaxSizeStrategy maxSizeStrategy, Long expirationTimeout,
                Listener<K> listener) {
            this.map = new ConcurrentHashMap<>();
            this.maxSize = maxSize;
            this.expirationTimeout = expirationTimeout;
            this.listener = listener;
            this.computingFunctionAdapter = (key) -> {
                if (maxSize != null && map.size() > maxSize) {
                    throw new MaxSizeExceededException();
                }
                return CacheEntry.of(computingFunction.compute(key));
            };
            this.maxSizeStrategy = maxSizeStrategy;
        }

        @Override
        public V get(K key) {
            CacheEntry<V> entry = compute(key);
            if (entry.isExpired(expirationTimeout)) {
                if (map.remove(key) != null) {
                    notifyListener(key, RemovalCause.EXPIRED);
                }
                entry = compute(key);
            }
            return entry.value;
        }

        @Override
        public V getIfPresent(K key) {
            CacheEntry<V> entry = map.get(key);
            if (entry != null) {
                if (entry.isExpired(expirationTimeout)) {
                    if (map.remove(key) != null) {
                        notifyListener(key, RemovalCause.EXPIRED);
                    }
                } else {
                    return entry.value;
                }
            }
            return null;
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public long size() {
            return map.size();
        }

        @Override
        public void invalidate(ComputingCache.KeyPredicate<K> predicate) {
            for (Iterator<K> iterator = map.keySet().iterator(); iterator
                    .hasNext();) {
                K key = iterator.next();
                if (predicate.apply(key)) {
                    iterator.remove();
                    notifyListener(key, RemovalCause.INVALIDATE);
                }
            }
        }

        @Override
        public Map<K, V> getAllPresent() {
            ImmutableMapBuilder<K, V> builder = ImmutableMap.builder();
            for (Entry<K, CacheEntry<V>> entry : map.entrySet()) {
                if (!entry.getValue().isExpired(expirationTimeout)) {
                    builder.put(entry.getKey(), entry.getValue().value);
                }
            }
            return builder.build();
        }

        private void notifyListener(K key, RemovalCause cause) {
            if (listener != null) {
                listener.entryInvalidated(key, cause.toString());
            }
        }

        private CacheEntry<V> compute(K key) {
            try {
                return map.computeIfAbsent(key, computingFunctionAdapter);
            } catch (MaxSizeExceededException e) {
                handleMaxSizeExceeding();
                // Theoretically, this may also throw MaxSizeExceededException
                // if the limit is exceeded before the value is computed, which
                // is unlikely.
                return map.computeIfAbsent(key, computingFunctionAdapter);
            }

        }

        private synchronized void handleMaxSizeExceeding() {
            if (map.size() > maxSize) {
                applyMaxSizeStrategy();
            }
        }

        private void applyMaxSizeStrategy() {
            switch (maxSizeStrategy) {
            case CLEAR:
                // Clearing the whole map is not quite elegant, but exceeding
                // the
                // limit should be an exteme situation
                LOGGER.debug(
                        "Max size limit of {} exceeded - removing all entries from the cache",
                        maxSize);
                if (listener != null) {
                    Set<K> keys = new HashSet<>(map.keySet());
                    map.clear();
                    for (K key : keys) {
                        notifyListener(key, RemovalCause.MAX_SIZE_EXCEEDED);
                    }
                } else {
                    map.clear();
                }
                break;
            default:
                LOGGER.warn(
                        "Max size limit of {} exceeded but the eviction strategy {} is not implemented!",
                        maxSize, maxSizeStrategy);
                break;
            }
        }

    }

    /**
     * Defines the strategy applied when the max size limit is set and exceeded.
     *
     * @author Martin Kouba
     */
    public enum MaxSizeStrategy {

        /**
         * Do nothing
         */
        NOOP(false),
        /**
         * Remove all entries from the cache
         */
        CLEAR(true),;

        MaxSizeStrategy(boolean isEvictionSupported) {
            this.isEvictionSupported = isEvictionSupported;
        }

        private boolean isEvictionSupported;

        public boolean isEvictionSupported() {
            return isEvictionSupported;
        }

    }

    public enum RemovalCause {

        EXPIRED,
        MAX_SIZE_EXCEEDED,
        INVALIDATE

    }

    /**
     * Signals that the max size limit was exceeded.
     *
     * @author Martin Kouba
     */
    private static class MaxSizeExceededException extends RuntimeException {

        private static final long serialVersionUID = 1L;

    }

    private static class CacheEntry<T> {

        private final Long createdAt;

        private final T value;

        static <T> CacheEntry<T> of(T value) {
            return new CacheEntry<>(System.currentTimeMillis(), value);
        }

        private CacheEntry(Long createdTs, T value) {
            this.createdAt = createdTs;
            this.value = value;
        }

        boolean isExpired(Long expirationTimeout) {
            return expirationTimeout != null && (System.currentTimeMillis()
                    - createdAt) >= expirationTimeout;
        }

    }

}
