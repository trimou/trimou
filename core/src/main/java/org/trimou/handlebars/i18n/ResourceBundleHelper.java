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

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.OptionsHashKeys;
import org.trimou.util.Arrays;

/**
 * <p>
 * First register the helper instance:
 * </p>
 * <code>
 * MustacheEngineBuilder.newBuilder().registerHelper("msg", new ResourceBundleHelper("messages")).build();
 * </code>
 *
 * <p>
 * Than use the helper in the template:
 * </p>
 * <code>
 * {{msg "my.key"}}
 * </code>
 *
 * <p>
 * The key need not be a string literal:
 * </p>
 * <code>
 * {{msg foo.key}}
 * </code>
 *
 * <p>
 * You may also override the default baseName:
 * </p>
 * <code>
 * {{msg "my.key" baseName="messages"}}
 * </code>
 *
 * <p>
 * And also use message parameters and {@link Formatter} or
 * {@link MessageFormat}:
 * </p>
 * <code>
 * hello.key=Hello %s!
 * </code>
 * <p/>
 * <code>
 * {{msg "hello.key" "world" format="printf"}}
 * </code>
 *
 * <p>
 * See also {@link Format} for more info about formats.
 * </p>
 *
 * <p>
 * Since 1.7 a custom {@link Locale} can be set via options hash with
 * {@link OptionsHashKeys#LOCALE} key. See also
 * {@link LocaleAwareValueHelper#getLocale(Options)}.
 * </p>
 *
 * <code>
 * {{msg "key" locale="fr"}}
 * </code>
 *
 * @author Martin Kouba
 * @see LocaleSupport
 */
public class ResourceBundleHelper extends LocaleAwareValueHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(ResourceBundleHelper.class);

    private static final String OPTION_KEY_BASE_NAME = "baseName";

    private static final String OPTION_KEY_FORMAT = "format";

    private final String defaultBaseName;

    private final Format defaultFormat;

    /**
     *
     * @param defaultBaseName
     */
    public ResourceBundleHelper(String defaultBaseName) {
        this(defaultBaseName, Format.PRINTF);
    }

    /**
     *
     * @param defaultBaseName
     * @param defaultFormat
     */
    public ResourceBundleHelper(String defaultBaseName, Format defaultFormat) {
        this.defaultBaseName = defaultBaseName;
        this.defaultFormat = defaultFormat;
    }

    @Override
    public void execute(Options options) {

        String key = options.getParameters().get(0).toString();
        String baseName = options.getHash().isEmpty()
                || !options.getHash().containsKey(OPTION_KEY_BASE_NAME) ? defaultBaseName
                : getHashValue(options, OPTION_KEY_BASE_NAME).toString();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName,
                getLocale(options));

        if (bundle.containsKey(key)) {

            Format format = getFormat(options.getHash());
            String stringValue = bundle.getString(key);

            if (Format.NO_FORMAT.equals(format)) {
                append(options, stringValue);
            } else {
                Object[] formatParams = getFormatParams(options.getParameters());
                try {
                    if (Format.PRINTF.equals(format)) {
                        append(options, String.format(bundle.getString(key),
                                formatParams));
                    } else if (Format.MESSAGE.equals(format)) {
                        append(options, MessageFormat.format(
                                bundle.getString(key), formatParams));
                    }
                } catch (Exception e) {
                    throw new MustacheException(
                            MustacheProblem.RENDER_IO_ERROR, e);
                }
            }
        }
    }

    private Format getFormat(Map<String, Object> hash) {
        if (hash.isEmpty() || !hash.containsKey(OPTION_KEY_FORMAT)) {
            return defaultFormat;
        }
        String customFormat = hash.get(OPTION_KEY_FORMAT).toString();
        Format format = Format.parse(customFormat);
        if (format == null) {
            logger.warn(
                    "Unsupported format specified: {}, using the default one: {}",
                    customFormat, defaultFormat.getValue());
            format = defaultFormat;
        }
        return format;
    }

    private Object[] getFormatParams(List<Object> params) {
        if (params.size() > 1) {
            return params.subList(1, params.size()).toArray();
        }
        return Arrays.EMPTY_OBJECT_ARRAY;
    }

    /**
     *
     * @author Martin Kouba
     */
    public static enum Format {

        /**
         * @see Formatter
         */
        PRINTF("printf"),
        /**
         * @see MessageFormat
         */
        MESSAGE("message"),
        /**
         * No formatting.
         */
        NO_FORMAT("none"), ;

        Format(String value) {
            this.value = value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        static Format parse(String value) {
            for (Format format : values()) {
                if (value.equals(format.value)) {
                    return format;
                }
            }
            return null;
        }

    }

}
