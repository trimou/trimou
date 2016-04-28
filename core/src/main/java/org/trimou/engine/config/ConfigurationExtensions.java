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
package org.trimou.engine.config;

import java.util.Map;
import java.util.Map.Entry;

import org.trimou.engine.config.ConfigurationExtension.ConfigurationExtensionBuilder;
import org.trimou.handlebars.Helper;
import org.trimou.util.Checker;

/**
 *
 * @author Martin Kouba
 */
public class ConfigurationExtensions {

    private ConfigurationExtensions() {
    }

    /**
     * Register helper and do nothing if a helper with the same name is already registered.
     *
     * @param builder
     * @param helpers
     */
    public static void registerHelpers(ConfigurationExtensionBuilder builder, Map<String, Helper> helpers) {
        Checker.checkArgumentsNotNull(builder, helpers);
        for (Entry<String, Helper> entry : helpers.entrySet()) {
            registerHelper(builder, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Register helpers and do nothing if a helper with the same name is already registered.
     *
     * @param builder
     * @param name
     * @param helper
     */
    public static void registerHelper(ConfigurationExtensionBuilder builder, String name, Helper helper) {
        Checker.checkArgumentsNotNull(builder, name, helper);
        try {
            builder.registerHelper(name, helper);
        } catch (IllegalArgumentException ignored) {
        }
    }

}
