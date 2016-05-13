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
package org.trimou.engine.config;

import static org.trimou.engine.config.ConfigurationExtensions.registerHelpers;

import org.trimou.engine.resolver.CombinedIndexResolver;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.engine.resolver.ThisResolver;
import org.trimou.handlebars.HelpersBuilder;

/**
 * Registers the default components.
 *
 * @author Martin Kouba
 */
public class DefaultConfigurationExtension implements ConfigurationExtension {

    public static final int DEFAULT_EXTENSION_PRIORITY = DEFAULT_PRIORITY + 10;

    @Override
    public int getPriority() {
        return DEFAULT_EXTENSION_PRIORITY;
    }

    @Override
    public void register(ConfigurationExtensionBuilder builder) {
        // Add built-in resolvers
        builder.addResolver(new ReflectionResolver());
        builder.addResolver(new ThisResolver());
        builder.addResolver(new MapResolver());
        builder.addResolver(new CombinedIndexResolver());
        // Add built-in helpers
        registerHelpers(builder, HelpersBuilder.builtin().build());
    }

}
