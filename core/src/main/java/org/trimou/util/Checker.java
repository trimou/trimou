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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Checker {

    private static final Long LONG_ZERO = Long.valueOf(0L);

    private static final Integer INTEGER_ZERO = Integer.valueOf(0);

    private static final Short SHORT_ZERO = Short.valueOf((short) 0);

    private static final Byte BYTE_ZERO = Byte.valueOf((byte) 0);

    private static final Double DOUBLE_ZERO = Double.valueOf(0.0d);

    private static final Float FLOAT_ZERO = Float.valueOf(0.0f);

    private Checker() {
    }

    public static void checkArgument(boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(errorMessage);
        }
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
        checkArgument(!Strings.isEmpty(argument),
                "String argument must not be null or empty");
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * @param value
     * @return <code>true</code> if the value is <code>null</code>, Boolean of
     *         value false, Number of value 0, or empty
     *         CharSequence/Collection/Array, <code>false</code> otherwise
     */
    @SuppressWarnings("rawtypes")
    public static boolean isFalsy(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof Boolean) {
            return !(Boolean) value;
        } else if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        } else if (value instanceof Iterable) {
            return !((Iterable) value).iterator().hasNext();
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        } else if (value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;
        } else if (value instanceof Number) {
            return isZero((Number) value);
        }
        return false;
    }

    private static boolean isZero(Number number) {
        if (number instanceof BigDecimal) {
            return BigDecimal.ZERO.compareTo((BigDecimal) number) == 0;
        }
        return INTEGER_ZERO.equals(number) || LONG_ZERO.equals(number)
                || SHORT_ZERO.equals(number) || BYTE_ZERO.equals(number)
                || DOUBLE_ZERO.equals(number) || FLOAT_ZERO.equals(number)
                || BigInteger.ZERO.equals(number);
    }
}
