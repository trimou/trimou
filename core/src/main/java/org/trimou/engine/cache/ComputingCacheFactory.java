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

import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.config.ConfigurationAware;

/**
 * A factory for {@link ComputingCache} instances. An implementation must be
 * thread-safe.
 *
 * It's always initialized before other {@link ConfigurationAware} components so
 * that other components can make use of computing cache API.
 *
 * @author Martin Kouba
 * @since 1.7
 */
public interface ComputingCacheFactory extends ConfigurationAware {

    /**
     * The {@link ComputingCache} implementations are not required to implement
     * eviction operations (expiration timeout, maximum size, listener). However, a
     * {@link ComputingCacheFactory} must either log a warning message or throw a
     * runtime exception in such cases.
     *
     * @param consumerId
     *            Allow the factory to identify the cache consumer
     * @param computingFunction
     * @param expirationTimeout
     *            Expiration timeout in milliseconds
     * @param maxSize
     *            Maximum size of the cache, subsequent eviction operation is
     *            implementation-specific
     * @param listener
     * @return a new cache
     */
    <K, V> ComputingCache<K, V> create(String consumerId,
            Function<K, V> computingFunction, Long expirationTimeout,
            Long maxSize, ComputingCache.Listener<K> listener);

}
