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
package org.trimou.cdi;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 *
 * @author Martin Kouba
 */
final class SecurityActions {

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
     * @param clazz
     * @return {@link Class#getMethod(String, Class...)}
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    static Method getMethod(final Class<?> clazz, final String name,
            final Class<?>... parameterTypes) throws NoSuchMethodException,
            SecurityException, PrivilegedActionException {
        if (System.getSecurityManager() == null) {
            return clazz.getMethod(name, parameterTypes);
        }
        return AccessController
                .doPrivileged(new PrivilegedExceptionAction<Method>() {
                    @Override
                    public Method run() throws Exception {
                        return clazz.getMethod(name, parameterTypes);
                    }
                });
    }

}
