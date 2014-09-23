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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.cache.ComputingCacheFactory;
import org.trimou.engine.config.ConfigurationExtension;
import org.trimou.engine.config.ConfigurationExtension.ConfigurationExtensionBuilder;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.interpolation.KeySplitter;
import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.text.TextSupport;
import org.trimou.handlebars.Helper;
import org.trimou.util.Checker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for {@link MustacheEngine}. It's not thread-safe until
 * {@link #build()} method is called - the builder is considered immutable
 * afterwards and subsequent invocations of any modifying method or
 * {@link #build()} results in {@link IllegalStateException}. Thus it's not
 * reusable.
 *
 * @author Martin Kouba
 */
public final class MustacheEngineBuilder implements
        ConfigurationExtensionBuilder {

    private static final Logger logger = LoggerFactory
            .getLogger(MustacheEngineBuilder.class);

    private boolean omitServiceLoaderConfigurationExtensions;

    private boolean isMutable;

    private final ImmutableSet.Builder<Resolver> resolversBuilder;

    private final ImmutableSet.Builder<TemplateLocator> templateLocators;

    private final ImmutableMap.Builder<String, Object> globalData;

    private TextSupport textSupport;

    private LocaleSupport localeSupport;

    private final ImmutableMap.Builder<String, Object> properties;

    private final List<EngineBuiltCallback> engineReadyCallbacks;

    private final ImmutableList.Builder<MustacheListener> mustacheListeners;

    private KeySplitter keySplitter;

    private MissingValueHandler missingValueHandler;

    private final ImmutableMap.Builder<String, Helper> helpers;

    private ComputingCacheFactory computingCacheFactory;

    /**
     * Don't create a new instance.
     */
    private MustacheEngineBuilder() {
        this.omitServiceLoaderConfigurationExtensions = false;
        this.isMutable = true;
        this.resolversBuilder = ImmutableSet.builder();
        this.templateLocators = ImmutableSet.builder();
        this.globalData = ImmutableMap.builder();
        this.properties = ImmutableMap.builder();
        this.mustacheListeners = ImmutableList.builder();
        this.helpers = ImmutableMap.builder();
        this.engineReadyCallbacks = new ArrayList<MustacheEngineBuilder.EngineBuiltCallback>();
    }

    /**
     * Builds the engine instance.
     *
     * @return the built engine
     */
    public MustacheEngine build() {
        checkIsMutable("build()");
        MustacheEngine engine = new DefaultMustacheEngine(this);
        for (EngineBuiltCallback callback : engineReadyCallbacks) {
            callback.engineBuilt(engine);
        }
        Package pack = MustacheEngine.class.getPackage();
        logger.info(
                "Engine built {}{}",
                StringUtils.isEmpty(pack.getSpecificationVersion()) ? "SNAPSHOT"
                        : pack.getSpecificationVersion(), engine
                        .getConfiguration().getInfo());
        isMutable = false;
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
        Checker.checkArgumentsNotNull(name, value);
        checkIsMutable("addGlobalData()");
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
        Checker.checkArgumentNotNull(locator);
        checkIsMutable("addTemplateLocator()");
        this.templateLocators.add(locator);
        return this;
    }

    /**
     * Adds a value resolver.
     *
     * @param resolver
     * @return self
     */
    public MustacheEngineBuilder addResolver(Resolver resolver) {
        Checker.checkArgumentNotNull(resolver);
        checkIsMutable("addResolver()");
        this.resolversBuilder.add(resolver);
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
        Checker.checkArgumentsNotNull(key, value);
        checkIsMutable("setProperty()");
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
    public <T extends ConfigurationKey> MustacheEngineBuilder setProperty(
            T configurationKey, Object value) {
        Checker.checkArgumentsNotNull(configurationKey, value);
        checkIsMutable("setProperty()");
        setProperty(configurationKey.get(), value);
        return this;
    }

    /**
     * Sets a text support instance.
     *
     * @param textSupport
     * @return self
     */
    public MustacheEngineBuilder setTextSupport(TextSupport textSupport) {
        Checker.checkArgumentNotNull(textSupport);
        checkIsMutable("setTextSupport()");
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
        Checker.checkArgumentNotNull(localeSupport);
        checkIsMutable("setLocaleSupport()");
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
        Checker.checkArgumentNotNull(callback);
        checkIsMutable("registerCallback()");
        this.engineReadyCallbacks.add(callback);
        return this;
    }

    /**
     * Adds a {@link Mustache} listener. Manually added listeners are always
     * registered before listeners added via configuration extensions.
     *
     * @param listener
     * @return self
     */
    public MustacheEngineBuilder addMustacheListener(MustacheListener listener) {
        Checker.checkArgumentNotNull(listener);
        checkIsMutable("addMustacheListener()");
        this.mustacheListeners.add(listener);
        return this;
    }

    /**
     *
     * @param keySplitter
     * @return self
     */
    public MustacheEngineBuilder setKeySplitter(KeySplitter keySplitter) {
        Checker.checkArgumentNotNull(keySplitter);
        checkIsMutable("setKeySplitter()");
        this.keySplitter = keySplitter;
        return this;
    }

    /**
     *
     * @param missingValueHandler
     * @return self
     */
    public MustacheEngineBuilder setMissingValueHandler(
            MissingValueHandler missingValueHandler) {
        Checker.checkArgumentNotNull(missingValueHandler);
        checkIsMutable("setMissingValueHandler()");
        this.missingValueHandler = missingValueHandler;
        return this;
    }

    /**
     * Each helper must be registered with a unique name. If there are more
     * helpers registered with the same name an {@link IllegalArgumentException}
     * is thrown during {@link #build()}.
     *
     * @param name
     * @param helper
     * @return self
     */
    public MustacheEngineBuilder registerHelper(String name, Helper helper) {
        Checker.checkArgumentsNotNull(name, helper);
        checkIsMutable("registerHelper()");
        this.helpers.put(name, helper);
        return this;
    }

    /**
     * Each helper must be registered with a unique name. If there are more
     * helpers registered with the same name an {@link IllegalArgumentException}
     * is thrown during {@link #build()}.
     *
     * @param helpers
     * @return self
     */
    public MustacheEngineBuilder registerHelpers(Map<String, Helper> helpers) {
        if (Checker.isNullOrEmpty(helpers)) {
            return this;
        }
        checkIsMutable("registerHelpers()");
        this.helpers.putAll(helpers);
        return this;
    }

    /**
     * Don't use the ServiceLoader mechanism to load configuration extensions.
     *
     * @see ConfigurationExtension
     */
    public MustacheEngineBuilder omitServiceLoaderConfigurationExtensions() {
        checkIsMutable("omitServiceLoaderConfigurationExtensions()");
        this.omitServiceLoaderConfigurationExtensions = true;
        return this;
    }

    /**
     * @param cacheFactory
     * @return self
     */
    public MustacheEngineBuilder setComputingCacheFactory(ComputingCacheFactory cacheFactory) {
        Checker.checkArgumentNotNull(cacheFactory);
        checkIsMutable("setCacheFactory()");
        this.computingCacheFactory = cacheFactory;
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
     * @see MustacheEngineBuilder#registerCallback(EngineBuiltCallback)
     */
    public interface EngineBuiltCallback {

        public void engineBuilt(MustacheEngine engine);

    }

    public Set<TemplateLocator> buildTemplateLocators() {
        return templateLocators.build();
    }

    public Set<Resolver> buildResolvers() {
        return resolversBuilder.build();
    }

    public Map<String, Object> buildGlobalData() {
        return globalData.build();
    }

    public TextSupport getTextSupport() {
        return textSupport;
    }

    public LocaleSupport getLocaleSupport() {
        return localeSupport;
    }

    public boolean isOmitServiceLoaderConfigurationExtensions() {
        return omitServiceLoaderConfigurationExtensions;
    }

    public Map<String, Object> buildProperties() {
        return properties.build();
    }

    public List<MustacheListener> buildMustacheListeners() {
        return mustacheListeners.build();
    }

    public KeySplitter getKeySplitter() {
        return keySplitter;
    }

    public MissingValueHandler getMissingValueHandler() {
        return missingValueHandler;
    }

    public Map<String, Helper> buildHelpers() {
        return helpers.build();
    }

    public ComputingCacheFactory getComputingCacheFactory() {
        return computingCacheFactory;
    }

    private void checkIsMutable(String methodName) {
        if (!isMutable) {
            throw new IllegalStateException(
                    "Invalid method invocation - builder already built: "
                            + methodName);
        }
    }

}
