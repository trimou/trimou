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

import org.trimou.engine.resolver.ArrayIndexResolver;
import org.trimou.engine.resolver.ListIndexResolver;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.engine.resolver.ThisResolver;
import org.trimou.handlebars.HelpersBuilder;

/**
 * Registers the default resolvers.
 *
 * @author Martin Kouba
 */
public class DefaultConfigurationExtension implements ConfigurationExtension {

    @Override
    public void register(ConfigurationExtensionBuilder builder) {
        // Add built-in resolvers
        builder.addResolver(new ReflectionResolver())
                .addResolver(new ThisResolver()).addResolver(new MapResolver())
                .addResolver(new ListIndexResolver())
                .addResolver(new ArrayIndexResolver());
        // Register built-in helpers
        builder.registerHelpers(HelpersBuilder.builtin().build());
    }

}
