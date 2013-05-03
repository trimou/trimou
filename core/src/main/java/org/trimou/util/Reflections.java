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

import static org.trimou.util.Checker.checkArgumentNull;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Kouba
 */
public final class Reflections {

	private static final Logger logger = LoggerFactory
			.getLogger(Reflections.class);

	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";

	private Reflections() {
	}

	/**
	 * If the name of the method starts with <b>get/is</b> prefix (JavaBean
	 * naming convention), the key in the map is the name of the corresponding
	 * property.
	 *
	 * @param clazz
	 * @return read methods map
	 */
	public static Map<String, Method> getReadMethods(Class<?> clazz) {

		long start = System.currentTimeMillis();
		checkArgumentNull(clazz);

		Method[] clazzMethods = clazz.getMethods();
		Map<String, Method> readMethods = new HashMap<String, Method>(
				clazzMethods.length);

		for (Method method : clazzMethods) {

			if (!isReadMethod(method)) {
				continue;
			}

			String name = method.getName();

			if (name.startsWith(GET_PREFIX)) {
				readMethods.put(
						Introspector.decapitalize(name.substring(3,
								name.length())), method);
			} else if (name.startsWith(IS_PREFIX)) {
				readMethods.put(
						Introspector.decapitalize(name.substring(2,
								name.length())), method);
			} else {
				readMethods.put(name, method);
			}
		}
		logger.debug(
				"Read methods [type: {}, found: {}, time: {} ms]",
				new Object[] { clazz.getName(), readMethods.size(),
						System.currentTimeMillis() - start });
		return readMethods;
	}

	/**
	 * If the name of the method starts with <b>get/is</b> prefix (JavaBean
	 * naming convention), the key in the map is the name of the corresponding
	 * property.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Method getReadMethod(Class<?> clazz, String name) {

		long start = System.currentTimeMillis();
		checkArgumentNull(clazz);
		checkArgumentNull(name);

		Method[] clazzMethods = clazz.getMethods();
		Method found = null;

		for (Method method : clazzMethods) {

			if (!isReadMethod(method)) {
				continue;
			}

			String methodName = method.getName();

			if (methodName.equals(name)
					|| (methodName.startsWith(GET_PREFIX) && Introspector
							.decapitalize(
									methodName.substring(3, methodName.length()))
							.equals(name))
					|| (methodName.startsWith(IS_PREFIX) && Introspector
							.decapitalize(
									methodName.substring(2, methodName.length()))
							.equals(name))) {

				found = method;
			}
		}
		logger.debug(
				"{} read method {}found [type: {}, time: {} ms]",
				new Object[] { name, found != null ? "" : "not ",
						clazz.getName(), System.currentTimeMillis() - start });
		return found;
	}

	/**
	 * A read method:
	 * <ul>
	 * <li>is public</li>
	 * <li>is non-static</li>
	 * <li>has no parameters</li>
	 * <li>has non-void return type</li>
	 * <li>its declaring class is not {@link Object}</li>
	 * </ul>
	 *
	 * @param method
	 * @return <code>true</code> if the given method is considered a read method
	 */
	public static boolean isReadMethod(Method method) {

		if (method == null) {
			return false;
		}

		// Skip non-static methods with no parameters and void return type
		if (Modifier.isStatic(method.getModifiers())
				|| method.getParameterTypes().length != 0
				|| method.getReturnType().equals(Void.TYPE)) {
			return false;
		}

		// Skip Object class methods
		if (Object.class.equals(method.getDeclaringClass())) {
			return false;
		}

		return true;
	}

}
