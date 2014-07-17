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
package org.trimou.engine.segment;

import java.util.Iterator;

import org.trimou.engine.resolver.Mapper;

/**
 * Iteration metadata.
 *
 * @author Martin Kouba
 */
public final class IterationMeta implements Mapper {

    private static final String KEY_INDEX = "iterIndex";

    private static final String KEY_HAS_NEXT = "iterHasNext";

    private static final String KEY_FIRST = "iterIsFirst";

    private static final String KEY_LAST = "iterIsLast";

    private final String alias;

    private final Iterator<?> iterator;

    private final int length;

    private int index;

    /**
     *
     * @param alias
     * @param iterator
     */
    public IterationMeta(String alias, Iterator<?> iterator) {
        this(alias, iterator, 0);
    }

    /**
     *
     * @param alias
     * @param length
     */
    public IterationMeta(String alias, int length) {
        this(alias, null, length);
    }

    private IterationMeta(String alias, Iterator<?> iterator, int length) {
        this.alias = alias;
        this.iterator = iterator;
        this.index = 1;
        this.length = length;
    }

    /**
     * The first element is at index <code>1</code>.
     *
     * @return the current iteration index
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @return <code>true</code> if the iteration has more elements,
     *         <code>false</code> otherwise
     */
    public boolean hasNext() {
        return iterator != null ? iterator.hasNext() : (index < length);
    }

    /**
     *
     * @return <code>true</code> for the first iteration, <code>false</code>
     *         otherwise
     */
    public boolean isFirst() {
        return index == 1;
    }

    /**
     *
     * @return <code>true</code> for the last iteration, <code>false</code>
     *         otherwise
     */
    public boolean isLast() {
        return iterator != null ? !iterator.hasNext() : (index == length);
    }

    /**
     *
     * @return <code>true</code> if the current index is odd, <code>false</code>
     *         otherwise
     */
    public boolean isOdd() {
        return !isEven();
    }

    /**
     *
     * @return <code>true</code> if the current index is even,
     *         <code>false</code> otherwise
     */
    public boolean isEven() {
        return index % 2 == 0;
    }

    public void nextIteration() {
        index++;
    }

    @Override
    public Object get(String key) {
        if (alias.equals(key)) {
            return this;
        }
        // Preserved for backwards compatibility
        if (KEY_INDEX.equals(key)) {
            return getIndex();
        } else if (KEY_HAS_NEXT.equals(key)) {
            return hasNext();
        } else if (KEY_FIRST.equals(key)) {
            return isFirst();
        } else if (KEY_LAST.equals(key)) {
            return isLast();
        }
        return null;
    }

}
