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
package org.trimou.util;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Arrays {

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     *
     * @param array
     * @param object
     * @return <code>true</code> if the given array contains the specified
     *         object
     */
    public static boolean contains(Object[] array, Object object) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if ((object != null && object.equals(array[i]))
                    || (object == null && array[i] == null)) {
                return true;
            }
        }
        return false;
    }
}
