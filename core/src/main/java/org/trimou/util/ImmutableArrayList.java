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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Immutable list implementation backed by an array.
 *
 * @author Martin Kouba
 */
class ImmutableArrayList<E> extends AbstractList<E> {

    private final Object[] elements;

    ImmutableArrayList(Object[] elements) {
        this.elements = elements;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= elements.length) {
            indexOutOfBound(index);
        }
        return getInternal(index);
    }

    @SuppressWarnings("unchecked")
    E getInternal(int index) {
        return (E) elements[index];
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public Iterator<E> iterator() {
        return new ImmutableListIterator(elements.length);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ImmutableListIterator(elements.length);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > elements.length) {
            indexOutOfBound(index);
        }
        return new ImmutableListIterator(elements.length, index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex) {
            indexOutOfBound(fromIndex);
        }
        if (toIndex > elements.length) {
            indexOutOfBound(toIndex);
        }
        if (fromIndex == toIndex) {
            return Collections.emptyList();
        }
        return new ImmutableArrayList<>(
                Arrays.copyOfRange(this.elements, fromIndex, toIndex));
    }

    @Override
    public String toString() {
        return Arrays.toString(elements);
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return elements.length == 0;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED
                | Spliterator.IMMUTABLE | Spliterator.NONNULL);
    }

    void indexOutOfBound(int index) {
        throw new IndexOutOfBoundsException("Index " + index
                + " is out of bounds, list size: " + elements.length);
    }

    private class ImmutableListIterator implements ListIterator<E> {

        private int cursor;

        private final int size;

        ImmutableListIterator(int size, int position) {
            this.size = size;
            this.cursor = position;
        }

        ImmutableListIterator(int size) {
            this(size, 0);
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return getInternal(cursor++);
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return getInternal(--cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }

}
