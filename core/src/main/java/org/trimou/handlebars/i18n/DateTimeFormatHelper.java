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

import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.resolver.i18n.DateTimeFormatResolver;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Options;

/**
 * This is an alternative to {@link DateTimeFormatResolver}. The main advantage
 * lies in the ability to specify custom pattern per tag: <code>
 * {{formatTime now pattern="DD-MM-yyyy HH:mm"}}
 * {{formatTime now pattern="HH:mm"}}
 * ...
 * </code>
 *
 * @author Martin Kouba
 */
public class DateTimeFormatHelper extends LocaleAwareValueHelper {

    private static final String OPTION_KEY_PATTERN = "pattern";

    private static final String OPTION_KEY_STYLE = "style";

    @Override
    public void execute(Options options) {

        Object formattableObject = getFormattableObject(options);

        if (options.getHash().isEmpty()) {
            appendStyle(options, formattableObject, DateFormat.MEDIUM);
        } else {
            if (options.getHash().containsKey(OPTION_KEY_PATTERN)) {
                appendCustom(options, formattableObject);
            } else if (options.getHash().containsKey(OPTION_KEY_STYLE)) {
                appendStyle(options, formattableObject);
            }
        }
    }

    private void appendStyle(Options options, Object formattableObject) {
        appendStyle(
                options,
                formattableObject,
                parseDateFormatStyle(getHashValue(options, OPTION_KEY_STYLE)
                        .toString(), options.getTagInfo()));
    }

    private void appendStyle(Options options, Object formattableObject,
            int style) {
        append(options, format(style, formattableObject));
    }

    private void appendCustom(Options options, Object object) {
        append(options,
                new SimpleDateFormat(options.getHash().get(OPTION_KEY_PATTERN)
                        .toString(), getCurrentLocale()).format(object));
    }

    private Object getFormattableObject(Options options) {

        Object value = options.getParameters().get(0);

        if (value instanceof Date || value instanceof Number) {
            return value;
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "Formattable date time object not found [template: %s, line: %s]",
                    options.getTagInfo().getTemplateName(), options
                            .getTagInfo().getLine());
        }
    }

    private String format(int style, Object object) {
        return DateFormat.getDateTimeInstance(style, style, getCurrentLocale())
                .format(object);
    }

    private int parseDateFormatStyle(String style, MustacheTagInfo tagInfo) {
        if ("full".equals(style)) {
            return DateFormat.FULL;
        } else if ("long".equals(style)) {
            return DateFormat.LONG;
        } else if ("short".equals(style)) {
            return DateFormat.SHORT;
        } else if ("medium".equals(style)) {
            return DateFormat.MEDIUM;
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "Unknown style defined %s [template: %s, line: %s]", style,
                    tagInfo.getTemplateName(), tagInfo.getLine());
        }
    }

}
