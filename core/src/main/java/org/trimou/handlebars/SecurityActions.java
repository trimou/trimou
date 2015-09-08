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
package org.trimou.handlebars;

import java.lang.reflect.AccessibleObject;
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
     * @param accessibleObject
     */
    static void setAccessible(final AccessibleObject accessibleObject) {
        if (System.getSecurityManager() == null) {
            accessibleObject.setAccessible(true);
        }
        AccessController.doPrivileged(new PrivilegedAction<AccessibleObject>() {
            @Override
            public AccessibleObject run() {
                accessibleObject.setAccessible(true);
                return accessibleObject;
            }
        });
    }

    /**
     *
     * @return the TCCL
     */
    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController
                .doPrivileged(new PrivilegedAction<ClassLoader>() {
                    @Override
                    public ClassLoader run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
    }

    /**
     *
     * @return the ClassLoader for the given class
     */
    static ClassLoader getClassLoader(final Class<?> clazz) {
        if (System.getSecurityManager() == null) {
            return clazz.getClassLoader();
        }
        return AccessController
                .doPrivileged(new PrivilegedAction<ClassLoader>() {
                    @Override
                    public ClassLoader run() {
                        return clazz.getClassLoader();
                    }
                });
    }

}
