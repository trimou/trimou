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

import java.util.Locale;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Locales {

    private Locales() {
    }

    /**
     *
     * @param localeObject
     * @return the {@link Locale} derived from the given locale object or
     *         <code>null</code>
     */
    public static Locale getLocale(Object localeObject) {
        if (localeObject != null) {
            if (localeObject instanceof Locale) {
                return (Locale) localeObject;
            } else {
                return Locale.forLanguageTag(localeObject.toString());
            }
        }
        return null;
    }

}
