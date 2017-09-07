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

import java.beans.Introspector;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Reflections {

    private Reflections() {
    }

    /**
     *
     * @param methodName
     * @param prefix
     * @return the decapitalized method name
     */
    public static String decapitalize(String methodName, String prefix) {
        return Introspector.decapitalize(
                methodName.substring(prefix.length(), methodName.length()));
    }

    /**
     *
     * @param name
     * @param methodName
     * @param prefix
     * @return <code>true</code> if the method name starts with the prefix and
     *         the decapitalized part without the prefix equals to the given
     *         name
     */
    public static boolean matchesPrefix(String name, String methodName,
            String prefix) {
        return methodName.startsWith(prefix)
                && decapitalize(methodName, prefix).equals(name);
    }

    /**
     *
     * @param enumClazz
     * @param name
     * @return <code>true</code> if the specified enum declares a constant with the specified name
     */
    public static boolean isConstantName(Class<?> enumClazz, String name) {
        for (Object constant : enumClazz.getEnumConstants()) {
            if (name.equals(constant.toString())) {
                return true;
            }
        }
        return false;
    }

}
