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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.i18n.LocaleAwareValueHelper;
import org.trimou.prettytime.resolver.PrettyTimeResolver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Developers are encouraged to use this helper instead of
 * {@link PrettyTimeResolver} to avoid negative performance impact during
 * interpolation.
 *
 * A {@link MustacheException} is thrown in case of the passed parameter is not
 * a formattable object (i.e. Date, Calendar or Long).
 *
 * <code>
 * {{prettyTime now}}
 * </code>
 *
 * @author Martin Kouba
 */
public class PrettyTimeHelper extends LocaleAwareValueHelper {

    /**
     * Lazy loading cache of PrettyTime instances
     */
    private final LoadingCache<Locale, PrettyTime> prettyTimeCache;

    public PrettyTimeHelper() {
        this(new DefaultPrettyTimeFactory());
    }

    /**
     *
     * @param prettyTimeFactory
     */
    public PrettyTimeHelper(final PrettyTimeFactory prettyTimeFactory) {
        this.prettyTimeCache = CacheBuilder.newBuilder().maximumSize(10)
                .build(new CacheLoader<Locale, PrettyTime>() {
                    @Override
                    public PrettyTime load(Locale locale) throws Exception {
                        return prettyTimeFactory.createPrettyTime(locale);
                    }
                });
    }

    @Override
    public void execute(Options options) {
        append(options, prettyTimeCache.getUnchecked(getCurrentLocale())
                .format(getFormattableObject(options)));
    }

    private Date getFormattableObject(Options options) {

        Object value = options.getParameters().get(0);

        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        } else if (value instanceof Long) {
            return new Date((Long) value);
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "Formattable object for PrettyTime not found [template: %s, line: %s]",
                    options.getTagInfo().getTemplateName(), options
                            .getTagInfo().getLine());
        }
    }

}
