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
package org.trimou.handlebars.i18n;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.resolver.i18n.DateTimeFormatResolver;

/**
 * This is an alternative to {@link DateTimeFormatResolver}. The main advantage
 * lies in the ability to specify custom pattern per tag:
 *
 * <code>
 * {{formatTime now pattern="DD-MM-yyyy HH:mm"}}
 * {{formatTime now pattern="HH:mm"}}
 * ...
 * </code>
 *
 * @author Martin Kouba
 */
public class DateTimeFormatHelper extends
        AbstractTimeFormatHelper<Object, Integer> {

    @Override
    protected String defaultFormat(Object value, Locale locale,
            TimeZone timeZone) {
        return format(value, DateFormat.MEDIUM, locale, timeZone);
    }

    @Override
    protected String format(Object value, Integer style, Locale locale,
            TimeZone timeZone) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(style, style,
                locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(value);
    }

    @Override
    protected String format(Object value, String pattern, Locale locale,
            TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
                locale);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(value);
    }

    protected Object getFormattableObject(Object value, Locale locale,
            TimeZone timeZone, MustacheTagInfo tagInfo) {
        if (value instanceof Date || value instanceof Number) {
            return value;
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        } else {
            throw valueNotAFormattableObject(value, tagInfo);
        }
    }

    protected Integer parseStyle(String style, MustacheTagInfo tagInfo) {
        if ("full".equals(style)) {
            return DateFormat.FULL;
        } else if ("long".equals(style)) {
            return DateFormat.LONG;
        } else if ("short".equals(style)) {
            return DateFormat.SHORT;
        } else if ("medium".equals(style)) {
            return DateFormat.MEDIUM;
        } else {
            throw unknownStyle(style, tagInfo);
        }
    }

}
