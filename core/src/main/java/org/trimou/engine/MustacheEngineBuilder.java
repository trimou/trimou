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

import static org.trimou.util.Checker.checkArgumentNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.text.TextSupport;

/**
 * Builds a {@link MustacheEngine} instance.
 *
 * @author Martin Kouba
 */
public class MustacheEngineBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(MustacheEngineBuilder.class);

	private boolean omitServiceLoaderResolvers = false;

	private Set<Resolver> resolvers = null;

	private Set<TemplateLocator> templateLocators = null;

	private Map<String, Object> globalData = null;

	private TextSupport textSupport = null;

	private LocaleSupport localeSupport = null;

	private Map<String, Object> properties = new HashMap<String, Object>(
			EngineConfigurationKey.values().length);

	private List<EngineBuiltCallback> engineReadyCallbacks = null;

	/**
	 * Don't create a new instance.
	 */
	private MustacheEngineBuilder() {
	}

	/**
	 * Builds the engine instance. The builder cleanup is performed after the
	 * engine is built.
	 *
	 * @return the built engine
	 */
	public MustacheEngine build() {
		MustacheEngine engine = new DefaultMustacheEngine(this);
		if (engineReadyCallbacks != null) {
			for (EngineBuiltCallback callback : engineReadyCallbacks) {
				callback.engineBuilt(engine);
			}
		}
		Package pack = MustacheEngine.class.getPackage();
		logger.info(
				"Engine built {}\n{}",
				StringUtils.isEmpty(pack.getSpecificationVersion()) ? "SNAPSHOT"
						: pack.getSpecificationVersion(), engine.toString());
		performCleanup();
		return engine;
	}

	/**
	 * Adds a value (e.g. Lambda) that is available during execution of all
	 * templates.
	 *
	 * Global values have to be thread-safe.
	 *
	 * @param value
	 * @param name
	 * @return self
	 */
	public MustacheEngineBuilder addGlobalData(String name, Object value) {
		if (this.globalData == null) {
			this.globalData = new HashMap<String, Object>();
		}
		this.globalData.put(name, value);
		return this;
	}

	/**
	 * Adds a template locator.
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
	 * Adds a context object resolver.
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
	 * Sets a configuration property.
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
	 * Sets a configuration property.
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
	 * Sets a text support instance.
	 *
	 * @param textSupport
	 * @return selg
	 */
	public MustacheEngineBuilder setTextSupport(TextSupport textSupport) {
		this.textSupport = textSupport;
		return this;
	}

	/**
	 * Sets a locale support instance.
	 *
	 * @param localeSupport
	 * @return self
	 */
	public MustacheEngineBuilder setLocaleSupport(LocaleSupport localeSupport) {
		this.localeSupport = localeSupport;
		return this;
	}

	/**
	 * Callback is useful to configure a component instantiated before the
	 * engine is built.
	 *
	 * @param callback
	 * @return self
	 */
	public MustacheEngineBuilder registerCallback(EngineBuiltCallback callback) {
		checkArgumentNotNull(callback);
		if (this.engineReadyCallbacks == null) {
			this.engineReadyCallbacks = new ArrayList<MustacheEngineBuilder.EngineBuiltCallback>();
		}
		this.engineReadyCallbacks.add(callback);
		return this;
	}

	/**
	 * Don't use the ServiceLoader mechanism to load resolvers.
	 */
	public MustacheEngineBuilder omitServiceLoaderResolvers() {
		this.omitServiceLoaderResolvers = true;
		return this;
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

	public Set<TemplateLocator> getTemplateLocators() {
		return templateLocators;
	}

	public Set<Resolver> getResolvers() {
		return resolvers;
	}

	public Map<String, Object> getGlobalData() {
		return globalData;
	}

	public TextSupport getTextSupport() {
		return textSupport;
	}

	public LocaleSupport getLocaleSupport() {
		return localeSupport;
	}

	public boolean isOmitServiceLoaderResolvers() {
		return omitServiceLoaderResolvers;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	private void performCleanup() {
		this.omitServiceLoaderResolvers = false;
		this.resolvers = null;
		this.templateLocators = null;
		this.globalData = null;
		this.textSupport = null;
		this.localeSupport = null;
		this.properties.clear();
		this.engineReadyCallbacks = null;
	}

}