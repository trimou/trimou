/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.jdk8.handlebars.i18n;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.handlebars.i18n.AbstractTimeFormatHelper;
import org.trimou.handlebars.i18n.DateTimeFormatHelper;

/**
 * An alternative to {@link DateTimeFormatHelper} which makes use of java.time
 * package in JDK 8 (JSR-310). It supports new temporal types and should also be
 * less resource-intensive.
 *
 * @author Martin Kouba
 */
public class TimeFormatHelper extends
        AbstractTimeFormatHelper<TemporalAccessor, FormatStyle> {

    private ComputingCache<CacheKey, DateTimeFormatter> formatterCache;

    protected void init() {
        super.init();
        formatterCache = configuration.getComputingCacheFactory().create(
                "todo",
                new ComputingCache.Function<CacheKey, DateTimeFormatter>() {
                    @Override
                    public DateTimeFormatter compute(CacheKey key) {
                        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                        if (key.getPattern() != null) {
                            builder.appendPattern(key.getPattern());
                        } else if (key.getStyle() != null) {
                            builder.appendLocalized(key.getStyle(),
                                    key.getStyle());
                        }
                        return builder.toFormatter(key.getLocale()).withZone(
                                key.getTimeZone().toZoneId());
                    }
                }, null, null, null);
    }

    protected String defaultFormat(TemporalAccessor value, Locale locale,
            TimeZone timeZone) {
        return format(value, FormatStyle.MEDIUM, locale, timeZone);
    }

    @Override
    protected String format(TemporalAccessor value, FormatStyle style,
            Locale locale, TimeZone timeZone) {
        return formatterCache.get(new CacheKey(locale, timeZone, null, style))
                .format(value);
    }

    @Override
    protected String format(TemporalAccessor value, String pattern,
            Locale locale, TimeZone timeZone) {
        return formatterCache
                .get(new CacheKey(locale, timeZone, pattern, null)).format(
                        value);
    }

    protected FormatStyle parseStyle(String style, MustacheTagInfo tagInfo) {
        try {
            return FormatStyle.valueOf(style.toUpperCase());
        } catch (Exception e) {
            throw unknownStyle(style, tagInfo);
        }
    }

    // TemporalAccessor should not be widely used in application code, but this
    // should be safe
    protected TemporalAccessor getFormattableObject(Object value,
            Locale locale, TimeZone timeZone, MustacheTagInfo tagInfo) {

        if (value instanceof TemporalAccessor) {
            return (TemporalAccessor) value;
        } else if (value instanceof Date) {
            return LocalDateTime.ofInstant(((Date) value).toInstant(),
                    timeZone.toZoneId());
        } else if (value instanceof Calendar) {
            return LocalDateTime.ofInstant(((Calendar) value).toInstant(),
                    timeZone.toZoneId());
        } else if (value instanceof Number) {
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(((Number) value).longValue()),
                    timeZone.toZoneId());
        } else {
            throw valueNotAFormattableObject(value, tagInfo);
        }
    }

    private static final class CacheKey {

        private final Locale locale;

        private final TimeZone timeZone;

        private final String pattern;

        private final FormatStyle style;

        public CacheKey(Locale locale, TimeZone timeZone, String pattern,
                FormatStyle style) {
            this.locale = locale;
            this.timeZone = timeZone;
            this.pattern = pattern;
            this.style = style;
        }

        protected Locale getLocale() {
            return locale;
        }

        protected TimeZone getTimeZone() {
            return timeZone;
        }

        protected String getPattern() {
            return pattern;
        }

        protected FormatStyle getStyle() {
            return style;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((locale == null) ? 0 : locale.hashCode());
            result = prime * result
                    + ((pattern == null) ? 0 : pattern.hashCode());
            result = prime * result + ((style == null) ? 0 : style.hashCode());
            result = prime * result
                    + ((timeZone == null) ? 0 : timeZone.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheKey other = (CacheKey) obj;
            if (locale == null) {
                if (other.locale != null)
                    return false;
            } else if (!locale.equals(other.locale))
                return false;
            if (timeZone == null) {
                if (other.timeZone != null)
                    return false;
            } else if (!timeZone.equals(other.timeZone))
                return false;
            if (pattern == null) {
                if (other.pattern != null)
                    return false;
            } else if (!pattern.equals(other.pattern))
                return false;
            if (style != other.style)
                return false;
            return true;
        }

    }

}
