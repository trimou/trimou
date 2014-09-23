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
package org.trimou.engine.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.cache.ComputingCache.Listener;
import org.trimou.engine.config.AbstractConfigurationAware;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;

/**
 * A default computing cache factory producing computing cache implementations
 * backed by {@link LoadingCache}.
 *
 * @author Martin Kouba
 */
public class DefaultComputingCacheFactory extends AbstractConfigurationAware
        implements ComputingCacheFactory {

    @Override
    public <K, V> ComputingCache<K, V> create(final String consumerId,
            final Function<K, V> computingFunction,
            final Long expirationTimeout, final Long maxSize,
            final Listener<K> listener) {

        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        if (expirationTimeout != null && expirationTimeout > 0) {
            builder.expireAfterWrite(expirationTimeout, TimeUnit.MILLISECONDS);
        }
        if (maxSize != null) {
            builder.maximumSize(maxSize);
        }
        if (listener != null) {
            builder.removalListener(new RemovalListener<K, V>() {
                @Override
                public void onRemoval(RemovalNotification<K, V> notification) {
                    listener.entryInvalidated(notification.getKey(),
                            notification.getCause().toString());
                }
            });
        }
        return new LoadingCacheAdapter<K, V>(
                builder.build(new CacheLoaderAdapter<K, V>(computingFunction)));
    }

    protected static class LoadingCacheAdapter<K, V> implements
            ComputingCache<K, V> {

        private final LoadingCache<K, V> cache;

        protected LoadingCacheAdapter(LoadingCache<K, V> cache) {
            this.cache = cache;
        }

        @Override
        public V get(K key) {
            return cache.getUnchecked(key);
        }

        @Override
        public V getIfPresent(K key) {
            return cache.getIfPresent(key);
        }

        @Override
        public void clear() {
            cache.invalidateAll();
            cache.cleanUp();
        }

        @Override
        public long size() {
            return cache.size();
        }

        @Override
        public void invalidate(KeyPredicate<K> keyPredicate) {
            for (K key : cache.asMap().keySet()) {
                if (keyPredicate.apply(key)) {
                    cache.invalidate(key);
                }
            }
        }

        @Override
        public Map<K, V> getAllPresent() {
            return ImmutableMap.copyOf(cache.asMap());
        }

    }

    protected static class CacheLoaderAdapter<K, V> extends CacheLoader<K, V> {

        private final Function<K, V> computingFunction;

        protected CacheLoaderAdapter(Function<K, V> computingFunction) {
            this.computingFunction = computingFunction;
        }

        @Override
        public V load(K key) throws Exception {
            return computingFunction.compute(key);
        }

    }

}
