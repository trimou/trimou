/*
 * Copyright 2015 Martin Kouba
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
package org.trimou.handlebars;

import com.google.common.base.Predicate;

/**
 *
 * @author Martin Kouba
 */
public final class Filters {

    private Filters() {
    }

    /**
     * A generic filter interface.
     *
     * @see EachHelper
     */
    public static interface Filter {

        /**
         *
         * @param value
         * @return <code>true</code> if the value matches, <code>false</code> otherwise
         */
        boolean test(Object value);

    }

    /**
     *
     * @param predicate
     * @return a predicate to filter adapter
     */
    public static Filter from(final Predicate<Object> predicate) {
        return new Filter() {
            @Override
            public boolean test(Object value) {
                return predicate.apply(value);
            }
        };
    }

}
