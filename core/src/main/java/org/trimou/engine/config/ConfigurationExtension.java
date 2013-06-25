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

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.resolver.Resolver;

/**
 * Configuration extensions are automatically loaded during the engine
 * initialization, provided the ServiceLoader mechanism is not disabled.
 *
 * @author Martin Kouba
 * @see MustacheEngineBuilder#omitServiceLoaderConfigurationExtensions()
 */
public interface ConfigurationExtension {

	/**
	 * Allows to register additional configuration components, e.g.
	 * {@link Resolver} and {@link MustacheListener} instances.
	 *
	 * @param builder
	 */
	void register(ConfigurationExtensionBuilder builder);

	/**
	 *
	 * @author Martin Kouba
	 */
	public interface ConfigurationExtensionBuilder {

		/**
		 * Adds a value resolver.
		 *
		 * @param resolver
		 * @return self
		 */
		ConfigurationExtensionBuilder addResolver(Resolver resolver);

		/**
		 * Adds a {@link Mustache} listener.
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
	}
}
