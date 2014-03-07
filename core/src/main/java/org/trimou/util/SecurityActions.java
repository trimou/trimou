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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 *
 * @author Martin Kouba
 */
final class SecurityActions {

    /**
     *
     * @param clazz
     * @return {@link Class#getMethods()}
     */
    static Method[] getMethods(final Class<?> clazz) {

        if (System.getSecurityManager() == null) {
            return clazz.getMethods();
        }

        return AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return clazz.getMethods();
            }
        });
    }

    /**
     *
     * @param clazz
     * @return {@link Class#getFields()}
     */
    static Field[] getFields(final Class<?> clazz) {

        if (System.getSecurityManager() == null) {
            return clazz.getFields();
        }
        return AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
            @Override
            public Field[] run() {
                return clazz.getFields();
            }
        });
    }

}
