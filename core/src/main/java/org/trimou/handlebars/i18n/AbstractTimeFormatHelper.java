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
package org.trimou.handlebars.i18n;

import static org.trimou.handlebars.OptionsHashKeys.PATTERN;
import static org.trimou.handlebars.OptionsHashKeys.STYLE;
import static org.trimou.handlebars.OptionsHashKeys.TIME_ZONE;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Options;
import org.trimou.util.ImmutableSet;

/**
 * An abstract time formatting helper.
 *
 * @author Martin Kouba
 *
 * @param <F>
 *            The formattable object type
 * @param <S>
 *            The style type
 */
public abstract class AbstractTimeFormatHelper<F, S>
        extends LocaleAwareValueHelper {

    private static final Set<String> SUPPORTED_HASH_KEYS = ImmutableSet.of(PATTERN, STYLE, TIME_ZONE);

    private final TimeZone defaultTimeZone = TimeZone.getDefault();

    @Override
    public void execute(Options options) {

        Locale locale = getLocale(options);
        TimeZone timeZone = getTimeZone(options);

        F value = getFormattableObject(options.getParameters().get(0), locale,
                timeZone, options.getTagInfo());

        String text;
        Object styleOrPattern;

        if ((styleOrPattern = options.getHash().get(PATTERN)) != null) {
            text = format(value, styleOrPattern.toString(), locale, timeZone);
        } else if ((styleOrPattern = options.getHash().get(STYLE)) != null) {
            text = format(value,
                    parseStyle(styleOrPattern.toString(), options.getTagInfo()),
                    locale, timeZone);
        } else {
            text = defaultFormat(value, locale, timeZone);
        }
        // There's no need to escape the formatted text
        options.append(text);
    }

    @Override
    protected Optional<Set<String>> getSupportedHashKeys() {
        return Optional.of(SUPPORTED_HASH_KEYS);
    }

    /**
     *
     * @param value
     * @return the default formatted string
     */
    protected abstract String defaultFormat(F value, Locale locale,
            TimeZone timeZone);

    /**
     *
     * @param value
     * @param style
     * @return the formatted string for the given style
     */
    protected abstract String format(F value, S style, Locale locale,
            TimeZone timeZone);

    /**
     *
     * @param value
     * @param pattern
     * @return the formatted string for the given pattern
     */
    protected abstract String format(F value, String pattern, Locale locale,
            TimeZone timeZone);

    /**
     *
     * @param style
     * @param tagInfo
     * @return the style
     */
    protected abstract S parseStyle(String style, MustacheTagInfo tagInfo);

    /**
     *
     * @param value
     * @param tagInfo
     * @return the formattable object
     * @throws MustacheException
     *             If the given value does not represent a formattable object
     */
    protected abstract F getFormattableObject(Object value, Locale locale,
            TimeZone timeZone, MustacheTagInfo tagInfo);

    /**
     * @param options
     * @return the timezone specified in the options hash, or the default one
     */
    protected TimeZone getTimeZone(Options options) {

        TimeZone timeZone;
        Object timeZoneObject = options.getHash().get(TIME_ZONE);

        if (timeZoneObject != null) {
            if (timeZoneObject instanceof TimeZone) {
                timeZone = (TimeZone) timeZoneObject;
            } else {
                timeZone = TimeZone.getTimeZone(timeZoneObject.toString());
            }
        } else {
            timeZone = defaultTimeZone;
        }
        return timeZone;
    }

    protected MustacheException valueNotAFormattableObject(Object value,
            MustacheTagInfo tagInfo) {
        return new MustacheException(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                "Value is not a formattable object [value: %s, template: %s, line: %s]",
                value, tagInfo.getTemplateName(), tagInfo.getLine());
    }

    protected MustacheException unknownStyle(String style,
            MustacheTagInfo tagInfo) {
        return new MustacheException(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                "Unknown style defined %s [template: %s, line: %s]", style,
                tagInfo.getTemplateName(), tagInfo.getLine());
    }

}
