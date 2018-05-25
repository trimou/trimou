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
package org.trimou.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 *
 */
@Internal
public final class Iterables {

    private Iterables() {
    }

    /**
     *
     * @param iterable
     * @return the size
     */
    public static int size(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).size();
        }
        Iterator<?> iterator = iterable.iterator();
        int size = 0;
        while (iterator.hasNext()) {
            iterator.next();
            size++;
        }
        return size;
    }

    /**
     *
     * @param element
     * @return the singleton iterator
     */
    public static <T> Iterator<T> singletonIterator(final T element) {
        Checker.checkArgumentNotNull(element);
        return new Iterator<T>() {

            private AtomicBoolean hasNext = new AtomicBoolean(true);

            @Override
            public boolean hasNext() {
                return hasNext.get();
            }

            @Override
            public T next() {
                if (hasNext.get()) {
                    hasNext.set(false);
                    return element;
                }
                throw new NoSuchElementException();
            }
        };
    }

}
