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

/**
 * A simple abstraction for computing (lazy loading) cache. May not contain null
 * values. An implementation must be thread-safe.
 *
 * @author Martin Kouba
 *
 * @param <K>
 *            The key
 * @param <V>
 *            The value
 */
public interface ComputingCache<K, V> {

    /**
     *
     * @param key
     * @return a value for a key, computing the value if necessary
     */
    V get(K key);

    /**
     *
     * @param key
     * @return a value for a key or <code>null</code> if no such value exists
     */
    V getIfPresent(K key);

    /**
     * Clear the cache.
     */
    void clear();

    /**
     *
     * @return the number of cached entries
     */
    long size();

    /**
     * Invalidate all entries whose keys satisfy a predicate.
     *
     * @param keyPredicate
     */
    void invalidate(KeyPredicate<K> keyPredicate);


    /**
     * @return an immutable map of the values which are present in the cache at the time this method is called
     */
    Map<K, V> getAllPresent();

    /**
     *
     * @param <K>
     *            The key
     * @param <V>
     *            The value
     */
    interface Function<K, V> {

        /**
         * Compute a value for a key.
         *
         * @param key
         * @return the value for the given key, must not be null
         */
        V compute(K key);

    }

    /**
     *
     * @param <K>
     *            The key
     */
    interface KeyPredicate<K> {

        /**
         *
         * @param key
         * @return
         */
        boolean apply(K key);

    }

    /**
     *
     * @author Martin Kouba
     *
     * @param <K>
     *            The key
     */
    interface Listener<K> {

        void entryInvalidated(K key, String cause);

    }

}