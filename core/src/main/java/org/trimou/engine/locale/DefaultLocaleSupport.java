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
package org.trimou.engine.locale;

import java.util.Locale;

import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.engine.resolver.Mapper;
import org.trimou.util.Locales;

/**
 * A default {@link LocaleSupport} implementation.
 *
 * @author Martin Kouba
 */
public class DefaultLocaleSupport extends AbstractConfigurationAware
        implements LocaleSupport {

    public static final String DEFAULT_LOCALE_KEY = "locale";

    @Override
    public Locale getCurrentLocale() {
        // Return the default locale for this JVM
        return Locale.getDefault();
    }

    @Override
    public Locale getCurrentLocale(Mapper mapper) {
        Locale locale = Locales.getLocale(mapper.get(DEFAULT_LOCALE_KEY));
        if (locale == null) {
            locale = getCurrentLocale();
        }
        return locale;
    }

}
