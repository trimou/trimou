/*
 * Copyright 2017 Trimou Team
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
package org.trimou.spring4.i18n;

import static org.trimou.handlebars.OptionsHashKeys.LOCALE;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.OptionsHashKeys;
import org.trimou.handlebars.i18n.LocaleAwareValueHelper;
import org.trimou.util.Arrays;
import org.trimou.util.ImmutableSet;
import org.trimou.util.Strings;

/**
 * <p>
 * First register the helper instance:
 * </p>
 * <code>
 * MustacheEngineBuilder.newBuilder().registerHelper("msg", new SpringMessageSourceHelper(messageSource)).build();
 * </code>
 * <p>
 * <p>
 * Than use the helper in the template:
 * </p>
 * <code>
 * {{msg 'my.key'}}
 * </code>
 * <p>
 * A default message can be set via options hash with {@link SpringMessageSourceHelper#DEFAULT_MESSAGE}.
 * <p>
 * <code>
 * {{msg 'key' defaultMessage='my default message'}}
 * </code>
 * <p>
 * A custom {@link Locale} can be set via options hash with
 * {@link OptionsHashKeys#LOCALE} key. See also
 * {@link LocaleAwareValueHelper#getLocale(Options)}.
 * <p>
 * <code>
 * {{msg 'key' locale='fr'}}
 * </code>
 *
 * @see LocaleSupport
 */
public final class SpringMessageSourceHelper extends LocaleAwareValueHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMessageSourceHelper.class);
    private static final String DEFAULT_MESSAGE = "defaultMessage";

    private final MessageSource messageSource;

    /**
     * Creates an instance of {@link SpringMessageSourceHelper} with the given {@link MessageSource} instance.
     *
     * @param messageSource instance of {@link MessageSource} to read messages from
     */
    public SpringMessageSourceHelper(final MessageSource messageSource) {
        this.messageSource = Objects.requireNonNull(messageSource, "messageSource must not be null");
    }

    @Override
    public void execute(final Options options) {
        final String msgCode = options.getParameters().get(0).toString();
        final String defaultMessage = getDefaultMessage(options.getHash());
        final Locale locale = getLocale(options);
        final Object[] msgArguments = getMessageArguments(options.getParameters());
        try {
            if (Strings.isEmpty(defaultMessage)) {
                append(options, messageSource.getMessage(msgCode, msgArguments, locale));
            } else {
                append(options, messageSource.getMessage(msgCode, msgArguments, defaultMessage, locale));
            }
        } catch (NoSuchMessageException e) {
            LOGGER.warn("Message code {} has not been found", msgCode);
            append(options, msgCode);
        }
    }

    @Override
    protected Set<String> getSupportedHashKeys() {
        return ImmutableSet.of(LOCALE, DEFAULT_MESSAGE);
    }

    private String getDefaultMessage(final Map<String, Object> hash) {
        final Object value = hash.get(DEFAULT_MESSAGE);
        return value == null ? null : value.toString();
    }

    private Object[] getMessageArguments(final List<Object> params) {
        if (params.size() > 1) {
            return params.subList(1, params.size()).toArray();
        }
        return Arrays.EMPTY_OBJECT_ARRAY;
    }
}
