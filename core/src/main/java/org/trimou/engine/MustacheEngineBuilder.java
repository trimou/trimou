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
import static org.trimou.util.Checker.checkArgumentsNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.cache.ComputingCacheFactory;
import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.config.ConfigurationExtension;
import org.trimou.engine.config.ConfigurationExtension.ConfigurationExtensionBuilder;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.convert.ValueConverter;
import org.trimou.engine.id.IdentifierGenerator;
import org.trimou.engine.interpolation.KeySplitter;
import org.trimou.engine.interpolation.LiteralSupport;
import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.text.TextSupport;
import org.trimou.handlebars.Helper;
import org.trimou.util.ImmutableList;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableSet;
import org.trimou.util.Strings;

/**
 * A builder for {@link MustacheEngine}. It's not thread-safe. The builder is
 * considered immutable once the {@link #build()} method is called. Subsequent
 * invocations of any modifying method or {@link #build()} result in an
 * {@link IllegalStateException}.
 * <p>
 * Note that most {@link ConfigurationAware} components are tied to the specific
 * engine instance and cannot be reused as well.
 *
 * @author Martin Kouba
 */
public final class MustacheEngineBuilder
        implements ConfigurationExtensionBuilder {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MustacheEngineBuilder.class);

    private static final String BUILD_PROPERTIES_FILE = "/trimou-build.properties";

    private boolean isBuilt;

    private boolean omitServiceLoaderConfigurationExtensions;

    private final Set<Resolver> resolvers;

    private final Set<TemplateLocator> templateLocators;

    private final Map<String, Object> globalData;

    private TextSupport textSupport;

    private LocaleSupport localeSupport;

    private final Map<String, Object> properties;

    private final List<EngineBuiltCallback> engineReadyCallbacks;

    private final List<MustacheListener> mustacheListeners;

    private KeySplitter keySplitter;

    private MissingValueHandler missingValueHandler;

    private final Map<String, Helper> helpers;

    private ComputingCacheFactory computingCacheFactory;

    private IdentifierGenerator identifierGenerator;

    private ExecutorService executorService;

    private LiteralSupport literalSupport;

    private ClassLoader configurationExtensionClassLoader;

    private final Set<ValueConverter> valueConverters;

    /**
     * Don't create a new instance.
     *
     * @see #newBuilder()
     */
    private MustacheEngineBuilder() {
        this.omitServiceLoaderConfigurationExtensions = false;
        this.resolvers = new HashSet<Resolver>();
        this.templateLocators = new HashSet<TemplateLocator>();
        this.globalData = new HashMap<String, Object>();
        this.properties = new HashMap<String, Object>();
        this.mustacheListeners = new ArrayList<MustacheListener>();
        this.helpers = new HashMap<String, Helper>();
        this.engineReadyCallbacks = new ArrayList<MustacheEngineBuilder.EngineBuiltCallback>();
        this.valueConverters = new HashSet<>();
    }

    /**
     * Builds the engine instance.
     *
     * @return the built engine
     */
    public synchronized MustacheEngine build() {

        MustacheEngine engine = new DefaultMustacheEngine(this);
        for (EngineBuiltCallback callback : engineReadyCallbacks) {
            callback.engineBuilt(engine);
        }

        String version = null;
        String timestamp = null;

        try {
            // First try to get trimou-build.properties file
            InputStream in = MustacheEngineBuilder.class
                    .getResourceAsStream(BUILD_PROPERTIES_FILE);
            if (in != null) {
                try {
                    Properties buildProperties = new Properties();
                    buildProperties.load(in);
                    version = buildProperties.getProperty("version");
                    timestamp = buildProperties.getProperty("timestamp");
                } finally {
                    in.close();
                }
            }
        } catch (IOException e) {
            // No-op
        }
        if (version == null) {
            // If not available use the manifest info
            Package pack = MustacheEngineBuilder.class.getPackage();
            version = pack.getSpecificationVersion();
            timestamp = pack.getImplementationVersion();
        }
        if (Strings.isEmpty(version)) {
            version = "SNAPSHOT";
            timestamp = "n/a";
        }

        int idx = timestamp.indexOf('T');
        if (idx > 0) {
            timestamp = timestamp.substring(0, idx);
        }

        LOGGER.info("Engine built {} ({})", version, timestamp);
        LOGGER.debug("Engine configuration: {}",
                engine.getConfiguration().getInfo());
        isBuilt = true;
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
        checkArgumentsNotNull(name, value);
        checkNotBuilt();
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
        checkArgumentNotNull(locator);
        checkNotBuilt();
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
        checkArgumentNotNull(resolver);
        checkNotBuilt();
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
        checkArgumentsNotNull(key, value);
        checkNotBuilt();
        this.properties.put(key, value);
        return this;
    }

    /**
     * Sets a configuration property.
     *
     * @param configurationKey
     * @param value
     * @param <T>
     *            The type of configuration key
     * @return self
     */
    public <T extends ConfigurationKey> MustacheEngineBuilder setProperty(
            T configurationKey, Object value) {
        checkArgumentsNotNull(configurationKey, value);
        checkNotBuilt();
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
        checkArgumentNotNull(textSupport);
        checkNotBuilt();
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
        checkArgumentNotNull(localeSupport);
        checkNotBuilt();
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
    public MustacheEngineBuilder registerCallback(
            EngineBuiltCallback callback) {
        checkArgumentNotNull(callback);
        checkNotBuilt();
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
    public MustacheEngineBuilder addMustacheListener(
            MustacheListener listener) {
        checkArgumentNotNull(listener);
        checkNotBuilt();
        this.mustacheListeners.add(listener);
        return this;
    }

    /**
     *
     * @param keySplitter
     * @return self
     */
    public MustacheEngineBuilder setKeySplitter(KeySplitter keySplitter) {
        checkArgumentNotNull(keySplitter);
        checkNotBuilt();
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
        checkArgumentNotNull(missingValueHandler);
        checkNotBuilt();
        this.missingValueHandler = missingValueHandler;
        return this;
    }

    /**
     * Each helper must be registered with a unique name. If there are more
     * helpers registered with the same name an {@link IllegalArgumentException}
     * is thrown. Use {@link #registerHelper(String, Helper, boolean)} to
     * overwrite the helper.
     *
     * @param name
     * @param helper
     * @return self
     */
    public MustacheEngineBuilder registerHelper(String name, Helper helper) {
        return registerHelper(name, helper, false);
    }

    /**
     * Each helper must be registered with a unique name. If there is a helper
     * registered with the same name and the param <code>overwrite</code> is
     * <code>true</code> the previous instance is replaced, otherwise an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param name
     * @param helper
     * @param overwrite
     * @return self
     */
    public MustacheEngineBuilder registerHelper(String name, Helper helper,
            boolean overwrite) {
        checkArgumentsNotNull(name, helper);
        checkNotBuilt();
        if (!overwrite && helpers.containsKey(name)) {
            throw new IllegalArgumentException(
                    "A helper with this name is already registered: " + name);
        }
        helpers.put(name, helper);
        return this;
    }

    /**
     * Each helper must be registered with a unique name. If there are more
     * helpers registered with the same name an {@link IllegalArgumentException}
     * is thrown. Use {@link #registerHelpers(Map, boolean)} to overwrite the
     * helpers.
     *
     * @param helpers
     * @return self
     */
    public MustacheEngineBuilder registerHelpers(Map<String, Helper> helpers) {
        return registerHelpers(helpers, false);
    }

    /**
     * Each helper must be registered with a unique name. If there is a helper
     * registered with the same name and the param <code>overwrite</code> is
     * <code>true</code> the previous instance is replaced, otherwise an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param helpers
     * @param overwrite
     * @return
     */
    public MustacheEngineBuilder registerHelpers(Map<String, Helper> helpers,
            boolean overwrite) {
        checkArgumentNotNull(helpers);
        checkNotBuilt();
        for (Entry<String, Helper> entry : helpers.entrySet()) {
            registerHelper(entry.getKey(), entry.getValue(), overwrite);
        }
        return this;
    }

    /**
     * Don't use the ServiceLoader mechanism to load configuration extensions
     * (i.e. the default resolvers are not added automatically).
     *
     * @see ConfigurationExtension
     */
    public MustacheEngineBuilder omitServiceLoaderConfigurationExtensions() {
        checkNotBuilt();
        this.omitServiceLoaderConfigurationExtensions = true;
        return this;
    }

    /**
     * Set the custom {@link ComputingCacheFactory}.
     *
     * @param cacheFactory
     * @return self
     */
    public MustacheEngineBuilder setComputingCacheFactory(
            ComputingCacheFactory cacheFactory) {
        checkArgumentNotNull(cacheFactory);
        checkNotBuilt();
        this.computingCacheFactory = cacheFactory;
        return this;
    }

    /**
     * Set the custom {@link IdentifierGenerator}.
     *
     * @param identifierGenerator
     * @return self
     */
    public MustacheEngineBuilder setIdentifierGenerator(
            IdentifierGenerator identifierGenerator) {
        checkArgumentNotNull(identifierGenerator);
        checkNotBuilt();
        this.identifierGenerator = identifierGenerator;
        return this;
    }

    /**
     * Set the {@link ExecutorService} to be used for async tasks.
     *
     * @param executorService
     * @return self
     */
    public MustacheEngineBuilder setExecutorService(
            ExecutorService executorService) {
        checkArgumentNotNull(executorService);
        checkNotBuilt();
        this.executorService = executorService;
        return this;
    }

    /**
     * Set the custom {@link LiteralSupport}.
     *
     * @param literalSupport
     * @return self
     */
    public MustacheEngineBuilder setLiteralSupport(
            LiteralSupport literalSupport) {
        checkArgumentNotNull(literalSupport);
        checkNotBuilt();
        this.literalSupport = literalSupport;
        return this;
    }

    /**
     * Set the {@link ClassLoader} used to load {@link ConfigurationExtension}s.
     *
     * @param configurationExtensionClassLoader
     * @return self
     */
    public MustacheEngineBuilder setConfigurationExtensionClassLoader(
            ClassLoader configurationExtensionClassLoader) {
        checkArgumentNotNull(configurationExtensionClassLoader);
        checkNotBuilt();
        this.configurationExtensionClassLoader = configurationExtensionClassLoader;
        return this;
    }

    /**
     * Add a value converter.
     *
     * @param converter
     * @return self
     */
    public MustacheEngineBuilder addValueConverter(ValueConverter converter) {
        checkArgumentNotNull(converter);
        checkNotBuilt();
        this.valueConverters.add(converter);
        return this;
    }

    /**
     *
     * @return a new instance of builder
     */
    public static MustacheEngineBuilder newBuilder() {
        return new MustacheEngineBuilder();
    }

    /**
     *
     * @author Martin Kouba
     * @see MustacheEngineBuilder#registerCallback(EngineBuiltCallback)
     */
    @FunctionalInterface
    public interface EngineBuiltCallback {

        public void engineBuilt(MustacheEngine engine);

    }

    public Set<TemplateLocator> buildTemplateLocators() {
        return ImmutableSet.copyOf(templateLocators);
    }

    public Set<Resolver> buildResolvers() {
        return ImmutableSet.copyOf(resolvers);
    }

    public Map<String, Object> buildGlobalData() {
        return ImmutableMap.copyOf(globalData);
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
        return ImmutableMap.copyOf(properties);
    }

    public List<MustacheListener> buildMustacheListeners() {
        return ImmutableList.copyOf(mustacheListeners);
    }

    public KeySplitter getKeySplitter() {
        return keySplitter;
    }

    public MissingValueHandler getMissingValueHandler() {
        return missingValueHandler;
    }

    public Map<String, Helper> buildHelpers() {
        return ImmutableMap.copyOf(helpers);
    }

    public ComputingCacheFactory getComputingCacheFactory() {
        return computingCacheFactory;
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public LiteralSupport getLiteralSupport() {
        return literalSupport;
    }

    public ClassLoader getConfigurationExtensionClassLoader() {
        return configurationExtensionClassLoader;
    }

    public Set<ValueConverter> buildValueConverters() {
        return ImmutableSet.copyOf(valueConverters);
    }

    private void checkNotBuilt() {
        if (isBuilt) {
            throw new IllegalStateException(
                    "Invalid method invocation - builder already built!");
        }
    }

}
