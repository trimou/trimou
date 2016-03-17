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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 *
 */
@Internal
public class ImmutableList {

    /**
     *
     * @param list
     * @return an immutable copy of the given list
     */
    public static <T> List<T> copyOf(List<T> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        if (list.size() == 1) {
            return Collections.singletonList(list.get(0));
        }
        return Collections.unmodifiableList(new ArrayList<T>(list));
    }

    /**
     *
     * @param elements
     * @return an immutable list of the given elements
     */
    @SafeVarargs
    public static <T> List<T> of(T... elements) {
        if (elements.length == 0) {
            return Collections.emptyList();
        }
        if (elements.length == 1) {
            return Collections.singletonList(elements[0]);
        }
        List<T> list = new ArrayList<>(elements.length);
        for (T element : elements) {
            list.add(element);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     *
     * @return a builder
     */
    public static <T> ImmutableListBuilder<T> builder() {
        return new ImmutableListBuilder<>();
    }

    public static final class ImmutableListBuilder<T> {

        private List<T> elements;

        private ImmutableListBuilder() {
            this.elements = new ArrayList<>();
        }

        public ImmutableListBuilder<T> add(T element) {
            elements.add(element);
            return this;
        }

        public List<T> build() {
            return copyOf(elements);
        }

    }

}
