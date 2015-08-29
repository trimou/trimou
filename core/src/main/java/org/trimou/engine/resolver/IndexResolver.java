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

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract index-based resolver.
 *
 * @author Martin Kouba
 * @see ListIndexResolver
 * @see ArrayIndexResolver
 */
public abstract class IndexResolver extends AbstractResolver {

    private static final Logger logger = LoggerFactory
            .getLogger(IndexResolver.class);

    public IndexResolver(int priority) {
        super(priority);
    }

    /**
     * @param name
     * @return <code>true</code> if the given key doesn't represent an index
     *         (must only contain digits)
     */
    protected boolean notAnIndex(String name) {
        return !isAnIndex(name);
    }

    /**
     * @param name
     * @return <code>true</code> if the given key represents an index (must only
     *         contain digits)
     */
    protected boolean isAnIndex(String name) {
        return NumberUtils.isDigits(name);
    }

    /**
     *
     * @param name
     * @param maxSize
     * @return the index value or <code>null</code> in case of invalid index
     */
    protected Integer getIndexValue(String name, int maxSize) {
        return getIndexValue(name, null, maxSize);
    }

    /**
     *
     * @param name
     * @param key
     * @param maxSize
     * @return the index value or <code>null</code> in case of invalid index
     */
    protected Integer getIndexValue(String name, String key, int maxSize) {

        final Integer index;

        try {
            index = Integer.valueOf(name);
        } catch (NumberFormatException e) {
            // Index is not an integer
            logger.warn("Index '{}' is not a valid integer value, key: '{}'",
                    name, maxSize, key != null ? key : "n/a");
            return null;
        }

        if (index < 0 || index >= maxSize) {
            // Index out of bound
            logger.warn(
                    "Trying to access index {} but the list/array has only {} elements, key: '{}'",
                    index, maxSize, key != null ? key : "n/a");
            return null;
        }
        return index;
    }

}
