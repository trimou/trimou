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
package org.trimou.engine.priority;

import java.util.Comparator;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Priorities {

    public static int rightAfter(int priority) {
        return after(priority, 1);
    }

    public static int rightBefore(int priority) {
        return before(priority, 1);
    }

    public static int after(int priority, int gap) {
        return priority - gap;
    }

    public static int before(int priority, int gap) {
        return priority + gap;
    }

    public static <T extends WithPriority> Comparator<T> higherFirst() {
        return Comparator.<T> comparingInt((e) -> e.getPriority()).reversed();
    }

    public static <T extends WithPriority> Comparator<T> lowerFirst() {
        return Comparator.<T> comparingInt((e) -> e.getPriority());
    }
}
