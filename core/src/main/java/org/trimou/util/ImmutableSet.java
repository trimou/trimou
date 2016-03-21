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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 *
 */
@Internal
public class ImmutableSet {

    /**
     *
     * @param set
     * @return an immutable copy of the given set
     */
    public static <T> Set<T> copyOf(Set<T> set) {
        if (set.isEmpty()) {
            return Collections.emptySet();
        }
        if (set.size() == 1) {
            return Collections.singleton(set.iterator().next());
        }
        return Collections.unmodifiableSet(new HashSet<>(set));
    }

    /**
     *
     * @param elements
     * @return an immutable set of the given elements
     */
    @SafeVarargs
    public static <T> Set<T> of(T... elements) {
        if (elements.length == 0) {
            return Collections.emptySet();
        }
        if (elements.length == 1) {
            return Collections.singleton(elements[0]);
        }
        Set<T> set = new HashSet<>();
        for (T element : elements) {
            set.add(element);
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     *
     * @return a builder
     */
    public static <T> ImmutableSetBuilder<T> builder() {
        return new ImmutableSetBuilder<>();
    }

    public static final class ImmutableSetBuilder<T> {

        private Set<T> elements;

        private ImmutableSetBuilder() {
            this.elements = new HashSet<>();
        }

        public ImmutableSetBuilder<T> add(T element) {
            elements.add(element);
            return this;
        }

        public Set<T> build() {
            return copyOf(elements);
        }

    }

}
