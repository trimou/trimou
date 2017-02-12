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

import java.util.Map;

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.convert.ValueConverter;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.Resolver;
import org.trimou.handlebars.Helper;

/**
 * Configuration extensions are automatically loaded during the engine
 * initialization, provided the ServiceLoader mechanism is not disabled.
 * <p>
 * Extensions with higher priority are registered first.
 *
 * @author Martin Kouba
 * @see MustacheEngineBuilder#omitServiceLoaderConfigurationExtensions()
 */
public interface ConfigurationExtension extends WithPriority {

    int DEFAULT_PRIORITY = 10;

    /**
     * Allows to register additional configuration components, e.g.
     * {@link Resolver} and {@link MustacheListener} instances.
     *
     * @param builder
     */
    void register(ConfigurationExtensionBuilder builder);

    @Override
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     *
     * @author Martin Kouba
     */
    interface ConfigurationExtensionBuilder {

        /**
         * Adds a value resolver.
         *
         * @param resolver
         * @return self
         */
        ConfigurationExtensionBuilder addResolver(Resolver resolver);

        /**
         * Adds a {@link Mustache} listener. Extension listeners are always
         * registered after listeners added via
         * {@link MustacheEngineBuilder#addMustacheListener(MustacheListener)}.
         * Further ordering is not defined.
         *
         * @param listener
         * @return self
         */
        ConfigurationExtensionBuilder addMustacheListener(
                MustacheListener listener);

        /**
         * Adds a value (e.g. Lambda) that is available during execution of all
         * templates.
         *
         * @param name
         * @param value
         * @return self
         */
        ConfigurationExtensionBuilder addGlobalData(String name, Object value);

        /**
         * Register a helper instance.
         *
         * @param name
         * @param helper
         * @return self
         * @throws IllegalArgumentException
         *             if a helper with the same name is already registered
         * @see ConfigurationExtensions#registerHelper(ConfigurationExtensionBuilder,
         *      String, Helper)
         */
        ConfigurationExtensionBuilder registerHelper(String name,
                Helper helper);

        /**
         * Register a helper instance.
         *
         * @param name
         * @param helper
         * @param overwrite
         * @return self
         */
        ConfigurationExtensionBuilder registerHelper(String name, Helper helper,
                boolean overwrite);

        /**
         * Register all the helper instances.
         *
         * @param helpers
         * @return self
         * @throws IllegalArgumentException
         *             if a helper with the same name is already registered
         * @see ConfigurationExtensions#registerHelpers(ConfigurationExtensionBuilder,
         *      Map)
         */
        ConfigurationExtensionBuilder registerHelpers(
                Map<String, Helper> helpers);

        /**
         * Register all the helper instances.
         *
         * @param helpers
         * @return self
         */
        ConfigurationExtensionBuilder registerHelpers(
                Map<String, Helper> helpers, boolean overwrite);

        /**
         * Adds a value converter.
         *
         * @param converter
         * @return self
         * @since 2.1
         */
        ConfigurationExtensionBuilder addValueConverter(ValueConverter converter);

    }
}
