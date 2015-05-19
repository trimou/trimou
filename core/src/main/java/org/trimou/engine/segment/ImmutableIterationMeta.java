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

import org.trimou.engine.resolver.Mapper;

/**
 * An immutable iteration metadata. This is a replacement for
 * {@link IterationMeta} which can't be used in asynchronous scenarios.
 *
 * @author Martin Kouba
 */
public final class ImmutableIterationMeta implements Mapper {

    private final String alias;

    private final int size;

    private final int index;

    /**
     *
     * @param alias
     * @param size
     * @param index
     */
    public ImmutableIterationMeta(String alias, int size, int index) {
        this.alias = alias;
        this.size = size;
        this.index = index;
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
     * The name for {@link #getIndex()} was poorly chosen. We should have used
     * <code>getCount()</code> instead. But we can't change it now - it would
     * break backward compatibility.
     *
     * @return the position of the current element, the first element has
     *         position <code>0</code>
     */
    public int getPosition() {
        return index - 1;
    }

    /**
     *
     * @return <code>true</code> if the iteration has more elements,
     *         <code>false</code> otherwise
     */
    public boolean hasNext() {
        return index < size;
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
        return index == size;
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

    @Override
    public Object get(String key) {
        if (alias.equals(key)) {
            return this;
        }
        // Preserved for backwards compatibility
        if (IterationMeta.KEY_INDEX.equals(key)) {
            return getIndex();
        } else if (IterationMeta.KEY_HAS_NEXT.equals(key)) {
            return hasNext();
        } else if (IterationMeta.KEY_FIRST.equals(key)) {
            return isFirst();
        } else if (IterationMeta.KEY_LAST.equals(key)) {
            return isLast();
        }
        return null;
    }

}
