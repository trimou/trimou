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

import static org.trimou.handlebars.OptionsHashKeys.LOCALE;

import java.util.Locale;
import java.util.Set;

import org.trimou.engine.locale.LocaleSupport;
import org.trimou.handlebars.BasicValueHelper;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.OptionsHashKeys;
import org.trimou.util.ImmutableSet;
import org.trimou.util.Locales;

/**
 * An abstract {@link Locale}-aware helper. Subclasses are encouraged to use
 * {@link #getLocale(Options)} to obtain the {@link Locale}.
 *
 * @author Martin Kouba
 */
public abstract class LocaleAwareValueHelper extends BasicValueHelper {

    private volatile LocaleSupport localeSupport;

    @Override
    protected void init() {
        super.init();
        localeSupport = configuration.getLocaleSupport();
    }

    @Override
    protected Set<String> getSupportedHashKeys() {
        return ImmutableSet.of(LOCALE);
    }

    /**
     *
     * @return the current locale by means of {@link LocaleSupport}
     */
    protected Locale getCurrentLocale() {
        return getCurrentLocale(null);
    }

    /**
     *
     * @param options
     * @return the current locale by means of {@link LocaleSupport}
     */
    protected Locale getCurrentLocale(Options options) {
        if (options != null) {
            return localeSupport.getCurrentLocale(options::getValue);
        }
        return localeSupport.getCurrentLocale();
    }

    /**
     *
     * @param options
     * @return the locale set via options hash with
     *         {@link OptionsHashKeys#LOCALE} key, the value from the context
     *         for the {@link OptionsHashKeys#LOCALE} key or the current locale
     *         by means of {@link LocaleSupport}
     * @see Locale#forLanguageTag(String)
     */
    protected Locale getLocale(Options options) {
        Locale locale;
        Object localeObject = options.getHash().get(LOCALE);
        if (localeObject == null) {
            // Keep this for backward compatibility - not all LocaleSupport
            // impls use LocaleSupport.getCurrentLocale(Mapper)
            localeObject = options.getValue(LOCALE);
        }
        if (localeObject != null) {
            locale = Locales.getLocale(localeObject);
        } else {
            locale = getCurrentLocale(options);
        }
        return locale;
    }

}
