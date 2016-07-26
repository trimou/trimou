/*
 * Copyright 2016 Martin Kouba
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
package org.trimou.handlebars;

import java.util.Formatter;
import java.util.Locale;

import org.trimou.handlebars.i18n.LocaleAwareValueHelper;

/**
 * A simple printf-style format helper.
 * <p>
 * The first param represents a format string and other params are arguments
 * (referenced by the format specifiers).
 *
 * <pre>
 * {{fmt 'Hello %s!' 'me'}}
 * </pre>
 *
 * A custom {@link Locale} can be set via options hash with
 * {@link OptionsHashKeys#LOCALE} key.
 *
 * <pre>
 * {{fmt '%tA' now locale='en'}}
 * </pre>
 *
 * @author Martin Kouba
 * @see Formatter
 */
public class FormatHelper extends LocaleAwareValueHelper {

    @Override
    protected int numberOfRequiredParameters() {
        return 2;
    }

    @SuppressWarnings("resource")
    @Override
    public void execute(Options options) {
        // We intentionally don't close the Formatter
        new Formatter(options.getAppendable(), getLocale(options))
                .format(options.getParameters().get(0).toString(),
                        options.getParameters()
                                .subList(1, options.getParameters().size())
                                .toArray());

    }

}
