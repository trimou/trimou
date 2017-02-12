/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.util;

import java.util.HashMap;
import java.util.Map;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Primitives {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER;

    private Primitives() {
    }

    static {
        Map<Class<?>, Class<?>> primitiveToWrapper = new HashMap<>();
        primitiveToWrapper.put(boolean.class, Boolean.class);
        primitiveToWrapper.put(char.class, Character.class);
        primitiveToWrapper.put(short.class, Short.class);
        primitiveToWrapper.put(int.class, Integer.class);
        primitiveToWrapper.put(long.class, Long.class);
        primitiveToWrapper.put(double.class, Double.class);
        primitiveToWrapper.put(float.class, Float.class);
        primitiveToWrapper.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER = ImmutableMap.copyOf(primitiveToWrapper);
    }

    /**
     *
     * @param type
     * @return the wrapper type of the given type if it is a primitive, or the
     *         type itself otherwise
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> type) {
        Checker.checkArgumentNotNull(type);
        Class<T> wrapped = (Class<T>) PRIMITIVE_TO_WRAPPER.get(type);
        return wrapped != null ? wrapped : type;
    }

}
