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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.cache.ComputingCacheFactory;
import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.cache.ComputingCache.Listener;
import org.trimou.engine.config.AbstractConfigurationAware;

import com.google.common.collect.ImmutableMap;

/**
 * A computing cache factory producing computing cache implementations backed by
 * {@link ConcurrentHashMap}. This implementation is a bit faster than the
 * default one using {@link com.google.common.cache.LoadingCache}. On the other
 * hand it does not support automatic eviction.
 *
 * @author Martin Kouba
 * @see Map#computeIfAbsent(Object, java.util.function.Function)
 */
public class ConcurrentHashMapComputingCacheFactory extends
        AbstractConfigurationAware implements ComputingCacheFactory {

    private static final Logger logger = LoggerFactory
            .getLogger(ConcurrentHashMapComputingCacheFactory.class);

    @Override
    public <K, V> ComputingCache<K, V> create(String consumerId,
            Function<K, V> computingFunction, Long expirationTimeout,
            Long maxSize, Listener<K> listener) {

        if (expirationTimeout != null) {
            logger.warn("Expiration timeout not supported - eviction will not be performed");
        }
        if (listener != null) {
            logger.warn("Listener not supported - notifications will not be delivered");
        }
        if (maxSize != null) {
            logger.warn("Max size not supported - eviction will not be performed");
        }
        return new ConcurrentHashMapAdapter<K, V>(
                new ConcurrentHashMap<K, V>(), computingFunction);
    }

    /**
     *
     * @author Martin Kouba
     *
     * @param <K>
     * @param <V>
     */
    public static class ConcurrentHashMapAdapter<K, V> implements
            ComputingCache<K, V> {

        private final ConcurrentHashMap<K, V> map;

        private final FunctionAdapter<K, V> computingFunctionAdapter;

        /**
         *
         * @param map
         * @param computingFunction
         */
        public ConcurrentHashMapAdapter(ConcurrentHashMap<K, V> map,
                ComputingCache.Function<K, V> computingFunction) {
            this.map = map;
            this.computingFunctionAdapter = new FunctionAdapter<K, V>(
                    computingFunction);
        }

        @Override
        public V get(K key) {
            return map.computeIfAbsent(key, computingFunctionAdapter);
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
            // Not an atomic action but it should not matter
            for (K key : map.keySet()) {
                if (keyPredicate.apply(key)) {
                    map.remove(key);
                }
            }
        }

        @Override
        public Map<K, V> getAllPresent() {
            return ImmutableMap.<K, V> copyOf(map);
        }

    }

    /**
     *
     * @author Martin Kouba
     *
     * @param <K>
     * @param <V>
     */
    public static class FunctionAdapter<K, V> implements
            java.util.function.Function<K, V> {

        private final Function<K, V> computingFunction;

        /**
         *
         * @param computingFunction
         */
        public FunctionAdapter(Function<K, V> computingFunction) {
            this.computingFunction = computingFunction;
        }

        @Override
        public V apply(K key) {
            return computingFunction.compute(key);
        }

    }

}
