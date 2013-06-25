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
package org.trimou.engine;

import static org.trimou.util.Checker.checkArgumentNotEmpty;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.trimou.Mustache;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationFactory;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.listener.MustacheCompilationEvent;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.parser.Parser;
import org.trimou.engine.parser.ParserFactory;
import org.trimou.engine.parser.ParsingHandler;
import org.trimou.engine.parser.ParsingHandlerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Default Mustache engine.
 *
 * @author Martin Kouba
 */
class DefaultMustacheEngine implements MustacheEngine {

	private LoadingCache<String, Optional<Mustache>> templateCache;

	private Configuration configuration;

	private Parser parser;

	/**
	 * Make this type proxyable (CDI) so that it's possible to produce
	 * application scoped CDI bean
	 */
	DefaultMustacheEngine() {
	}

	/**
	 *
	 * @param builder
	 */
	DefaultMustacheEngine(MustacheEngineBuilder builder) {

		configuration = new ConfigurationFactory().createConfiguration(builder);

		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();

		if (configuration
				.getBooleanPropertyValue(EngineConfigurationKey.DEBUG_MODE_ENABLED)) {
			// Disable template cache
			cacheBuilder.maximumSize(0);
		}

		// Template cache
		templateCache = cacheBuilder
				.build(new CacheLoader<String, Optional<Mustache>>() {

					@Override
					public Optional<Mustache> load(String key) throws Exception {

						if (configuration.getTemplateLocators() == null
								|| configuration.getTemplateLocators()
										.isEmpty()) {
							return Optional.absent();
						}

						Reader reader = null;

						for (TemplateLocator locator : configuration
								.getTemplateLocators()) {
							reader = locator.locate(key);
							if (reader != null) {
								break;
							}
						}

						if (reader == null) {
							return Optional.absent();
						}
						return Optional.of(parse(key, reader));
					}
				});

		// Init parser
		parser = new ParserFactory().createParser(this);

		// Precompile templates
		if (configuration
				.getBooleanPropertyValue(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES)) {

			Set<String> templateNames = new HashSet<String>();

			for (TemplateLocator locator : configuration.getTemplateLocators()) {
				templateNames.addAll(locator.getAllIdentifiers());
			}

			for (String templateName : templateNames) {
				getTemplateFromCache(templateName);
			}
		}
	}

	public Mustache getMustache(String templateName) {
		checkArgumentNotEmpty(templateName);
		return getTemplateFromCache(templateName);
	}

	public Mustache compileMustache(String templateName, String templateContent) {
		checkArgumentNotEmpty(templateName);
		checkArgumentNotEmpty(templateContent);
		return parse(templateName, new StringReader(templateContent));
	}

	/**
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Invalidates the template cache.
	 */
	public void invalidateTemplateCache() {
		this.templateCache.invalidateAll();
	}

	/**
	 *
	 * @return
	 */
	public LoadingCache<String, Optional<Mustache>> getTemplateCache() {
		return templateCache;
	}

	/**
	 *
	 * @param templateName
	 * @param reader
	 * @return
	 */
	private Mustache parse(String templateName, Reader reader) {
		ParsingHandler handler = new ParsingHandlerFactory()
				.createParsingHandler();

		parser.parse(templateName, reader, handler);
		Mustache mustache = handler.getCompiledTemplate();

		if (configuration.getMustacheListeners() != null) {
			MustacheCompilationEvent event = new DefaultMustacheCompilationEvent(
					mustache);
			for (MustacheListener listener : configuration
					.getMustacheListeners()) {
				listener.compilationFinished(event);
			}
		}
		return mustache;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(this.getClass().getName());
		builder.append("]");
		builder.append(Strings.LINE_SEPARATOR);
		builder.append(configuration.toString());
		return builder.toString();
	}

	private Mustache getTemplateFromCache(String templateName) {
		try {
			return templateCache.get(templateName).orNull();
		} catch (ExecutionException e) {
			throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
					e);
		}
	}

	/**
	 *
	 * @author Martin Kouba
	 */
	private static class DefaultMustacheCompilationEvent implements
			MustacheCompilationEvent {

		private final Mustache mustache;

		/**
		 *
		 * @param mustache
		 */
		public DefaultMustacheCompilationEvent(Mustache mustache) {
			super();
			this.mustache = mustache;
		}

		@Override
		public Mustache getMustache() {
			return mustache;
		}

	}

}
