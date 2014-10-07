/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.jdk8.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.cache.ComputingCache.Listener;
import org.trimou.engine.cache.ComputingCacheFactory;
import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.util.Checker;

import com.google.common.collect.ImmutableMap;

/**
 * A computing cache factory producing computing cache implementations backed by
 * {@link ConcurrentHashMap}. This implementation is a bit faster than the
 * default one using {@link com.google.common.cache.LoadingCache}. On the other
 * hand it does not support automatic timeout eviction and listeners. Moreover
 * its size-based eviction is not so effective.
 *
 * @author Martin Kouba
 * @see Map#computeIfAbsent(Object, java.util.function.Function)
 */
public class MapBackedComputingCacheFactory extends AbstractConfigurationAware
        implements ComputingCacheFactory {

    private static final Logger logger = LoggerFactory
            .getLogger(MapBackedComputingCacheFactory.class);

    private final MaxSizeStrategy maxSizeStrategy;

    public MapBackedComputingCacheFactory() {
        this(MaxSizeStrategy.CLEAR);
    }

    /**
     *
     * @param maxSizeStrategy
     */
    public MapBackedComputingCacheFactory(MaxSizeStrategy maxSizeStrategy) {
        Checker.checkArgumentNotNull(maxSizeStrategy);
        this.maxSizeStrategy = maxSizeStrategy;
    }

    @Override
    public <K, V> ComputingCache<K, V> create(String consumerId,
            Function<K, V> computingFunction, Long expirationTimeout,
            Long maxSize, Listener<K> listener) {

        if (expirationTimeout != null) {
            throw new IllegalArgumentException(
                    "Expiration timeout not supported");
        }
        if (listener != null) {
            logger.warn("Listener not supported - notifications will not be delivered");
        }
        if (maxSize != null && !maxSizeStrategy.isEvictionSupported()) {
            throw new IllegalArgumentException(
                    "Max size limit not supported - use a different eviction strategy");
        }
        return new ConcurrentHashMapAdapter<K, V>(
                new ConcurrentHashMap<K, V>(), computingFunction, maxSize,
                maxSizeStrategy);
    }

    /**
     *
     * @author Martin Kouba
     *
     * @param <K>
     * @param <V>
     */
    private static class ConcurrentHashMapAdapter<K, V> implements
            ComputingCache<K, V> {

        private static final Logger logger = LoggerFactory
                .getLogger(ConcurrentHashMapAdapter.class);

        private final MaxSizeStrategy maxSizeStrategy;

        private final Long maxSize;

        private final ConcurrentHashMap<K, V> map;

        private final FunctionAdapter<K, V> computingFunctionAdapter;

        /**
         *
         * @param map
         * @param computingFunction
         * @param maxSize
         * @param maxSizeStrategy
         */
        ConcurrentHashMapAdapter(ConcurrentHashMap<K, V> map,
                ComputingCache.Function<K, V> computingFunction, Long maxSize,
                MaxSizeStrategy maxSizeStrategy) {
            this.map = map;
            this.maxSize = maxSize;
            this.computingFunctionAdapter = new FunctionAdapter<K, V>(
                    computingFunction, this);
            this.maxSizeStrategy = maxSizeStrategy;
        }

        @Override
        public V get(K key) {
            try {
                return compute(key);
            } catch (MaxSizeExceededException e) {
                handleMaxSizeExceeding();
                // Theoretically, this may also throw MaxSizeExceededException
                // if the limit is exceeded before the value is computed, which
                // is unlikely.
                return compute(key);
            }
        }

        @Override
        public V getIfPresent(K key) {
            return map.get(key);
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
        public void invalidate(ComputingCache.KeyPredicate<K> keyPredicate) {
            for (Iterator<K> iterator = map.keySet().iterator(); iterator
                    .hasNext();) {
                if (keyPredicate.apply(iterator.next())) {
                    iterator.remove();
                }
            }
        }

        @Override
        public Map<K, V> getAllPresent() {
            return ImmutableMap.<K, V> copyOf(map);
        }

        private V compute(K key) {
            return map.computeIfAbsent(key, computingFunctionAdapter);
        }

        private void handleMaxSizeExceeding() {
            synchronized (this) {
                if (map.size() > maxSize) {
                    applyMaxSizeStrategy();
                }
            }
        }

        private void applyMaxSizeStrategy() {
            switch (maxSizeStrategy) {
            case CLEAR:
                // Clearing the map is not quite elegant, but exceeding the
                // limit should be an exteme situation
                logger.debug(
                        "Max size limit of {} exceeded - removing all entries from the cache",
                        maxSize);
                map.clear();
                break;
            default:
                logger.warn(
                        "Max size limit of {} exceeded but the eviction strategy {} is not implemented!",
                        maxSize, maxSizeStrategy);
                break;
            }
        }

    }

    /**
     *
     * @author Martin Kouba
     *
     * @param <K>
     * @param <V>
     */
    private static class FunctionAdapter<K, V> implements
            java.util.function.Function<K, V> {

        private final Function<K, V> computingFunction;

        private final ConcurrentHashMapAdapter<K, V> mapAdapter;

        /**
         *
         * @param computingFunction
         * @param adapter
         */
        public FunctionAdapter(Function<K, V> computingFunction,
                ConcurrentHashMapAdapter<K, V> adapter) {
            this.computingFunction = computingFunction;
            this.mapAdapter = adapter;
        }

        @Override
        public V apply(K key) {
            // Note that computation must not attempt to update any other
            // mappings of the map - therefore we cannot perform eviction here
            if (mapAdapter.maxSize != null && mapAdapter.map.size() > mapAdapter.maxSize) {
                throw new MaxSizeExceededException();
            }
            return computingFunction.compute(key);
        }

    }

    /**
     * Defines the strategy applied when the max size limit is set and exceeded.
     *
     * @author Martin Kouba
     */
    public static enum MaxSizeStrategy {

        /**
         * Do nothing
         */
        NOOP(false),
        /**
         * Remove all entries from the cache
         */
        CLEAR(true), ;

        private MaxSizeStrategy(boolean isEvictionSupported) {
            this.isEvictionSupported = isEvictionSupported;
        }

        private boolean isEvictionSupported;

        public boolean isEvictionSupported() {
            return isEvictionSupported;
        }

    }

    /**
     * Signals that the max size limit was exceeded.
     *
     * @author Martin Kouba
     */
    private static class MaxSizeExceededException extends RuntimeException {

        private static final long serialVersionUID = 1L;

    }

}
