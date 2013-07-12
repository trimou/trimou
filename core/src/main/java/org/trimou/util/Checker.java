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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Checker {

    private Checker() {
    }

    public static void checkArgumentsNotNull(Object... arguments) {
        for (Object argument : arguments) {
            checkArgumentNotNull(argument);
        }
    }

    public static void checkArgumentNotNull(Object argument) {
        checkArgument(argument != null, "Argument must not be null");
    }

    public static void checkArgumentNotEmpty(String argument) {
        checkArgument(!com.google.common.base.Strings.isNullOrEmpty(argument));
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
