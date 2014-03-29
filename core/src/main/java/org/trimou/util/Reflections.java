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

import static org.trimou.util.Checker.checkArgumentNotNull;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Reflections {

    private static final Logger logger = LoggerFactory
            .getLogger(Reflections.class);

    public static final String GET_PREFIX = "get";
    public static final String IS_PREFIX = "is";

    private Reflections() {
    }

    /**
     * First tries to find a valid method with the same name, afterwards method
     * following JavaBean naming convention (the method starts with
     * <b>get/is</b> prefix).
     *
     * @param clazz
     * @param name
     * @return the found method or <code>null</code>
     * @see Reflections#isMethodValid(Method)
     */
    public static Method findMethod(Class<?> clazz, String name) {

        checkArgumentNotNull(clazz);
        checkArgumentNotNull(name);

        Method foundMatch = null;
        Method foundGetMatch = null;
        Method foundIsMatch = null;

        for (Method method : SecurityActions.getMethods(clazz)) {

            if (!isMethodValid(method)) {
                continue;
            }

            if (method.isBridge()) {
                logger.debug("Skipping bridge method {}", method);
                continue;
            }

            if (name.equals(method.getName())) {
                foundMatch = method;
            } else if (matchesPrefix(name, method.getName(), GET_PREFIX)) {
                foundGetMatch = method;
            } else if (matchesPrefix(name, method.getName(), IS_PREFIX)) {
                foundIsMatch = method;
            }
        }

        if (foundMatch == null) {
            foundMatch = (foundGetMatch != null ? foundGetMatch : foundIsMatch);
        }

        logger.debug("{} method {}found [type: {}]", new Object[] { name,
                foundMatch != null ? "" : "not ", clazz.getName() });
        return foundMatch;
    }

    /**
     * Tries to find a public field with the given name on the given class.
     *
     * @param clazz
     * @param name
     * @return the found field or <code>null</code>
     */
    public static Field findField(Class<?> clazz, String name) {

        checkArgumentNotNull(clazz);
        checkArgumentNotNull(name);

        Field found = null;

        for (Field field : SecurityActions.getFields(clazz)) {
            if (field.getName().equals(name)) {
                found = field;
            }
        }
        logger.debug("{} field {}found [type: {}]", new Object[] { name,
                found != null ? "" : "not ", clazz.getName() });
        return found;
    }

    /**
     * A read method:
     * <ul>
     * <li>is public</li>
     * <li>has no parameters</li>
     * <li>has non-void return type</li>
     * <li>its declaring class is not {@link Object}</li>
     * </ul>
     *
     * @param method
     * @return <code>true</code> if the given method is considered a read method
     */
    public static boolean isMethodValid(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 0
                && !method.getReturnType().equals(Void.TYPE)
                && !Object.class.equals(method.getDeclaringClass());
    }

    /**
     *
     * @param clazz
     * @return the found methods
     */
    public static Set<Method> getMethods(Class<?> clazz) {

        checkArgumentNotNull(clazz);
        Set<Method> found = new HashSet<Method>();

        for (Method method : SecurityActions.getMethods(clazz)) {

            if (!isMethodValid(method)) {
                continue;
            }

            if (method.isBridge()) {
                logger.warn("Skipping bridge method {0}", method);
                continue;
            }

            found.add(method);
        }
        logger.debug("{} methods found [type: {}]", new Object[] {
                found.size(), clazz.getName() });
        return found;
    }

    /**
     *
     * @param clazz
     * @return the found methods
     */
    public static Set<Field> getFields(Class<?> clazz) {

        checkArgumentNotNull(clazz);
        Set<Field> found = new HashSet<Field>();

        for (Field field : SecurityActions.getFields(clazz)) {
            found.add(field);
        }
        logger.debug("{} field found [type: {}]", new Object[] { found.size(),
                clazz.getName() });
        return found;
    }

    /**
     *
     * @param methodName
     * @param prefix
     * @return the decapitalized method name
     */
    public static String decapitalize(String methodName, String prefix) {
        return Introspector.decapitalize(methodName.substring(prefix.length(),
                methodName.length()));
    }

    private static boolean matchesPrefix(String name, String methodName,
            String prefix) {
        return methodName.startsWith(prefix)
                && decapitalize(methodName, prefix).equals(name);
    }

}
