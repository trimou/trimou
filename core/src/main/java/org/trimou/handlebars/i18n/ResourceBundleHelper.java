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

import java.util.ResourceBundle;

import org.trimou.handlebars.Options;

/**
 * <p>
 * First register the helper instance:
 * </p>
 * <code>
 * MustacheEngineBuilder.newBuilder().registerHelper("msg", new ResourceBundleHelper("messages")).build();
 * </code>
 * <p>
 * Than use the helper in template:
 * </p>
 * <code>
 * {{msg "my.key"}}
 * </code>
 * <p>
 * You may also override the default baseName:
 * </p>
 * <code>
 * {{msg "my.key" baseName="messages"}}
 * </code>
 *
 * @author Martin Kouba
 */
public class ResourceBundleHelper extends LocaleAwareValueHelper {

    private static final String OPTION_KEY_BASE_NAME = "baseName";

    private final String baseName;

    /**
     *
     * @param baseName
     */
    public ResourceBundleHelper(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public void execute(Options options) {

        String name = options.getParameters().get(0).toString();
        ResourceBundle bundle;

        if (!options.getHash().isEmpty()
                && options.getHash().containsKey(OPTION_KEY_BASE_NAME)) {
            bundle = ResourceBundle.getBundle(
                    options.getHash().get(OPTION_KEY_BASE_NAME).toString(),
                    getCurrentLocale());
        } else {
            bundle = ResourceBundle.getBundle(baseName, getCurrentLocale());
        }

        if (bundle.containsKey(name)) {
            options.append(bundle.getString(name));
        }
    }

}
