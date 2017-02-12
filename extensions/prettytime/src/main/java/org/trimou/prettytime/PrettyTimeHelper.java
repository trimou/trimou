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
package org.trimou.prettytime;

import static org.trimou.util.Checker.checkArgumentsNotNull;

import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.convert.Converter;
import org.trimou.engine.convert.ObjectToDateConverter;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.OptionsHashKeys;
import org.trimou.handlebars.i18n.LocaleAwareValueHelper;
import org.trimou.prettytime.resolver.PrettyTimeResolver;

/**
 * <p>
 * Developers are encouraged to use this helper instead of
 * {@link PrettyTimeResolver} to avoid the negative performance impact during
 * interpolation.
 * </p>
 *
 * <p>
 * A {@link MustacheException} is thrown in case of the passed parameter is not
 * a formattable object (i.e. Date, Calendar or Long).
 * </p>
 *
 * <code>
 * {{pretty now}}
 * </code>
 *
 * <p>
 * Since 1.7 a custom {@link Locale} can be set via options hash with
 * {@link OptionsHashKeys#LOCALE} key. See also
 * {@link LocaleAwareValueHelper#getLocale(Options)}.
 * </p>
 *
 * <code>
 * {{pretty now locale='fr'}}
 * </code>
 *
 * @author Martin Kouba
 */
public class PrettyTimeHelper extends LocaleAwareValueHelper {

    public static final String COMPUTING_CACHE_CONSUMER_ID = PrettyTimeHelper.class
            .getName();

    public static final String DEFAULT_NAME = "pretty";

    private final PrettyTimeFactory factory;

    private final Converter<Object, Date> converter;

    /**
     * Lazy loading cache of PrettyTime instances
     */
    private ComputingCache<Locale, PrettyTime> prettyTimeCache;

    public PrettyTimeHelper() {
        this(new DefaultPrettyTimeFactory());
    }

    /**
     *
     * @param prettyTimeFactory
     */
    public PrettyTimeHelper(PrettyTimeFactory prettyTimeFactory) {
        this(prettyTimeFactory, new ObjectToDateConverter());
    }

    /**
     *
     * @param factory
     * @param converter
     */
    private PrettyTimeHelper(PrettyTimeFactory factory,
            Converter<Object, Date> converter) {
        checkArgumentsNotNull(factory, converter);
        this.factory = factory;
        this.converter = converter;
    }

    @Override
    public void init(Configuration configuration) {
        super.init(configuration);
        prettyTimeCache = configuration.getComputingCacheFactory()
                .create(COMPUTING_CACHE_CONSUMER_ID, factory::createPrettyTime, null, 10L, null);
    }

    @Override
    public void execute(Options options) {
        Object param = options.getParameters().get(0);
        if (param == null) {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "PrettyTimeHelper - no instance to format [template: %s, line: %s, param: %s]",
                    options.getTagInfo().getTemplateName(),
                    options.getTagInfo().getLine());
        }
        Date value = converter.convert(param);
        if (value == null) {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "Unable to get java.util.Date instance for PrettyTime [template: %s, line: %s, param: %s]",
                    options.getTagInfo().getTemplateName(),
                    options.getTagInfo().getLine(), param);
        }
        append(options, prettyTimeCache.get(getLocale(options)).format(value));
    }

    /**
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @author Martin Kouba
     *
     */
    public static class Builder {

        private PrettyTimeFactory factory;

        private Converter<Object, Date> converter;

        /**
         *
         * @param factory
         * @return self
         * @see PrettyTimeFactory
         */
        public Builder setFactory(PrettyTimeFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * Might be useful to customize the conversion of the context object to
         * the {@link Date} instance.
         *
         * @param converter
         * @return self
         */
        public Builder setConverter(Converter<Object, Date> converter) {
            this.converter = converter;
            return this;
        }

        public PrettyTimeHelper build() {
            if (factory == null) {
                factory = new DefaultPrettyTimeFactory();
            }
            if (converter == null) {
                converter = new ObjectToDateConverter();
            }
            return new PrettyTimeHelper(factory, converter);
        }

    }

}
