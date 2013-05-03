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

import static org.trimou.util.Checker.checkArgumentNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.api.Lambda;
import org.trimou.api.engine.ConfigurationKey;
import org.trimou.api.engine.MustacheEngine;
import org.trimou.spi.engine.LocaleSupport;
import org.trimou.spi.engine.Resolver;
import org.trimou.spi.engine.TemplateLocator;
import org.trimou.spi.engine.TextSupport;

/**
 *
 * @author Martin Kouba
 */
public class MustacheEngineBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(MustacheEngineBuilder.class);

	private boolean omitServiceLoaderResolvers = false;

	private Set<Resolver> resolvers = null;

	private Set<TemplateLocator> templateLocators = null;

	private Map<String, Lambda> globalLambdas = null;

	private TextSupport textSupport;

	private LocaleSupport localeSupport;

	private Map<String, Object> properties = new HashMap<String, Object>(
			EngineConfigurationKey.values().length);

	private List<EngineBuiltCallback> engineReadyCallbacks;

	/**
	 * Don't create a new instance.
	 */
	private MustacheEngineBuilder() {
	}

	/**
	 *
	 * @param properties
	 * @return
	 */
	public MustacheEngine build() {
		MustacheEngine engine = new DefaultMustacheEngine(this);
		if(engineReadyCallbacks != null) {
			for (EngineBuiltCallback callback : engineReadyCallbacks) {
				callback.engineBuilt(engine);
			}
		}
		logger.info("Engine built... \n{}", engine.toString());
		return engine;
	}

	/**
	 * Add globally enabled lambda, available during execution of all templates.
	 *
	 * Global lambdas have to be thread-safe.
	 *
	 * @param lambda
	 * @param name
	 * @return self
	 */
	public MustacheEngineBuilder addGlobalLambda(Lambda lambda, String name) {
		if (this.globalLambdas == null) {
			this.globalLambdas = new HashMap<String, Lambda>();
		}
		this.globalLambdas.put(name, lambda);
		return this;
	}

	/**
	 *
	 * @param locator
	 * @return self
	 */
	public MustacheEngineBuilder addTemplateLocator(TemplateLocator locator) {
		if (this.templateLocators == null) {
			this.templateLocators = new HashSet<TemplateLocator>();
		}
		this.templateLocators.add(locator);
		return this;
	}

	/**
	 * Add a custom resolver.
	 *
	 * @param resolver
	 * @return self
	 */
	public MustacheEngineBuilder addResolver(Resolver resolver) {
		if (this.resolvers == null) {
			this.resolvers = new HashSet<Resolver>();
		}
		this.resolvers.add(resolver);
		return this;
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return self
	 */
	public MustacheEngineBuilder setProperty(String key, Object value) {
		this.properties.put(key, value);
		return this;
	}

	/**
	 *
	 * @param configurationKey
	 * @param value
	 * @return self
	 */
	public MustacheEngineBuilder setProperty(ConfigurationKey configurationKey,
			Object value) {
		setProperty(configurationKey.get(), value);
		return this;
	}

	/**
	 *
	 * @param textSupport
	 * @return selg
	 */
	public MustacheEngineBuilder setTextSupport(TextSupport textSupport) {
		this.textSupport = textSupport;
		return this;
	}

	/**
	 *
	 * @param localeSupport
	 * @return self
	 */
	public MustacheEngineBuilder setLocaleSupport(LocaleSupport localeSupport) {
		this.localeSupport = localeSupport;
		return this;
	}

	/**
	 * Callback is useful to configure a component instantiated before the engine is built.
	 *
	 * @param callback
	 * @return self
	 */
	public MustacheEngineBuilder registerCallback(EngineBuiltCallback callback) {
		checkArgumentNull(callback);
		if (this.engineReadyCallbacks == null) {
			this.engineReadyCallbacks = new ArrayList<MustacheEngineBuilder.EngineBuiltCallback>();
		}
		this.engineReadyCallbacks.add(callback);
		return this;
	}

	/**
	 * Do not use ServiceLoader to load resolvers.
	 */
	public void omitServiceLoaderResolvers() {
		this.omitServiceLoaderResolvers = true;
	}

	Set<TemplateLocator> getTemplateLocators() {
		return templateLocators;
	}

	Set<Resolver> getResolvers() {
		return resolvers;
	}

	Map<String, Lambda> getGlobalLambdas() {
		return globalLambdas;
	}

	TextSupport getTextSupport() {
		return textSupport;
	}

	LocaleSupport getLocaleSupport() {
		return localeSupport;
	}

	boolean isOmitServiceLoaderResolvers() {
		return omitServiceLoaderResolvers;
	}

	Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 *
	 * @return new instance of builder
	 */
	public static MustacheEngineBuilder newBuilder() {
		return new MustacheEngineBuilder();
	}

	/**
	 *
	 * @author Martin Kouba
	 */
	public interface EngineBuiltCallback {

		public void engineBuilt(MustacheEngine engine);

	}
}