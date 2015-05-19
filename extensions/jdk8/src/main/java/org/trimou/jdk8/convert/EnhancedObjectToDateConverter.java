/*
 * Copyright 2015 Martin Kouba
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
package org.trimou.jdk8.convert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.trimou.engine.convert.ObjectToDateConverter;

/**
 * Converts also {@link LocalDateTime} and {@link LocalDate} instances.
 *
 * @author Martin Kouba
 */
public class EnhancedObjectToDateConverter extends ObjectToDateConverter {

    /**
     *
     */
    public EnhancedObjectToDateConverter() {
        super();
    }

    /**
     *
     * @param pattern
     */
    public EnhancedObjectToDateConverter(String pattern) {
        super(pattern);
    }

    @Override
    public Date convert(Object value) {
        Date converted = super.convert(value);
        if (converted == null) {
            if (value instanceof LocalDateTime) {
                converted = Date.from(((LocalDateTime) value).atZone(
                        ZoneId.systemDefault()).toInstant());
            } else if (value instanceof LocalDate) {
                converted = Date.from(((LocalDate) value).atStartOfDay(
                        ZoneId.systemDefault()).toInstant());
            }
        }
        return converted;
    }

}
