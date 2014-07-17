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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.interpolation.DotKeySplitter;
import org.trimou.engine.interpolation.KeySplitter;
import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.engine.interpolation.NoOpMissingValueHandler;
import org.trimou.engine.interpolation.ThrowingExceptionMissingValueHandler;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locale.LocaleSupportFactory;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.priority.HighPriorityComparator;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.text.TextSupport;
import org.trimou.engine.text.TextSupportFactory;
import org.trimou.engine.validation.Validateable;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Helper;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Martin Kouba
 */
class DefaultConfiguration implements Configuration {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultConfiguration.class);

    private static final String RESOURCE_FILE = "/trimou.properties";

    private final List<TemplateLocator> templateLocators;

    private final List<Resolver> resolvers;

    private final Map<String, Object> globalData;

    private final TextSupport textSupport;

    private final LocaleSupport localeSupport;

    private final Map<String, Object> properties;

    private final List<MustacheListener> mustacheListeners;

    private final KeySplitter keySplitter;

    private final MissingValueHandler missingValueHandler;

    private final Map<String, Helper> helpers;

    /**
     *
     * @param builder
     */
    @SuppressWarnings("deprecation")
    DefaultConfiguration(MustacheEngineBuilder builder) {

        if (!builder.isOmitServiceLoaderConfigurationExtensions()) {
            // Process configuration extensions
            for (Iterator<ConfigurationExtension> iterator = ServiceLoader
                    .load(ConfigurationExtension.class).iterator(); iterator
                    .hasNext();) {
                iterator.next().register(builder);
            }
        }

        // Non-final components
        List<Resolver> resolvers = identifyResolvers(builder);
        List<MustacheListener> mustacheListeners = new ArrayList<MustacheListener>(
                builder.buildMustacheListeners());
        MissingValueHandler missingValueHandler = identifyMissingValueHandler(builder);
        Map<String, Helper> helpers = builder.buildHelpers();

        this.textSupport = identifyTextSupport(builder);
        this.localeSupport = identifyLocaleSupport(builder);
        this.keySplitter = identifyKeySplitter(builder);
        this.templateLocators = identifyTemplateLocators(builder);
        Map<String, Object> globalData = builder.buildGlobalData();
        if(globalData.isEmpty()) {
            this.globalData = null;
        } else {
            this.globalData = globalData;
        }

        // All configuration aware components must be availabe at this time
        // so that it's possible to collect all configuration keys
        Set<ConfigurationAware> components = ImmutableSet
                .<ConfigurationAware> builder()
                .addAll(resolvers)
                .add(textSupport)
                .add(localeSupport)
                .add(keySplitter)
                .addAll(templateLocators != null ? templateLocators
                        : Collections.<ConfigurationAware> emptySet())
                .addAll(mustacheListeners).addAll(helpers.values()).build();

        this.properties = initializeProperties(builder, components);

        if (getBooleanPropertyValue(EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM)) {
            logger.warn(
                    "{}.{} is deprecated, use appropriate MissingValueHandler instance instead",
                    EngineConfigurationKey.class.getSimpleName(),
                    EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM);
            // Simulate deprecated settings
            this.missingValueHandler = new ThrowingExceptionMissingValueHandler();
        } else {
            this.missingValueHandler = missingValueHandler;
        }

        if (!getBooleanPropertyValue(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED)) {
            this.helpers = Collections.emptyMap();
        } else {
            this.helpers = helpers;
        }

        initializeConfigurationAwareComponents(components);

        // Filter out invalid components
        removeInvalidComponents(resolvers);
        removeInvalidComponents(mustacheListeners);

        this.resolvers = ImmutableList.copyOf(resolvers);
        this.mustacheListeners = mustacheListeners.isEmpty() ? null
                : mustacheListeners;
    }

    @Override
    public List<Resolver> getResolvers() {
        return resolvers;
    }

    @Override
    public Map<String, Object> getGlobalData() {
        return globalData;
    }

    @Override
    public List<TemplateLocator> getTemplateLocators() {
        return templateLocators;
    }

    @Override
    public TextSupport getTextSupport() {
        return textSupport;
    }

    @Override
    public LocaleSupport getLocaleSupport() {
        return localeSupport;
    }

    @Override
    public List<MustacheListener> getMustacheListeners() {
        return mustacheListeners;
    }

    @Override
    public KeySplitter getKeySplitter() {
        return keySplitter;
    }

    @Override
    public MissingValueHandler getMissingValueHandler() {
        return missingValueHandler;
    }

    @Override
    public Map<String, Helper> getHelpers() {
        return helpers;
    }

    @Override
    public <T extends ConfigurationKey> Long getLongPropertyValue(
            T configurationKey) {

        Long value = (Long) properties.get(configurationKey.get());

        if (value == null) {
            value = (Long) configurationKey.getDefaultValue();
        }
        return value;
    }

    @Override
    public <T extends ConfigurationKey> Integer getIntegerPropertyValue(
            T configurationKey) {

        Integer value = (Integer) properties.get(configurationKey.get());

        if (value == null) {
            value = (Integer) configurationKey.getDefaultValue();
        }
        return value;
    }

    @Override
    public <T extends ConfigurationKey> String getStringPropertyValue(
            T configurationKey) {

        Object value = properties.get(configurationKey.get());

        if (value == null) {
            value = configurationKey.getDefaultValue();
        }
        return value.toString();
    }

    @Override
    public <T extends ConfigurationKey> Boolean getBooleanPropertyValue(
            T configurationKey) {

        Boolean value = (Boolean) properties.get(configurationKey.get());

        if (value == null) {
            value = (Boolean) configurationKey.getDefaultValue();
        }
        return value;
    }

    public String getInfo() {
        StringBuilder builder = new StringBuilder();
        if (templateLocators != null) {
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("[Template locators]");
            for (TemplateLocator locator : templateLocators) {
                builder.append(Strings.LINE_SEPARATOR);
                builder.append(locator.toString());
            }
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("----------");
        }
        if (resolvers != null) {
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("[Resolvers]");
            for (Resolver resolver : resolvers) {
                builder.append(Strings.LINE_SEPARATOR);
                builder.append(resolver.toString());
            }
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("----------");
        }
        builder.append(Strings.LINE_SEPARATOR);
        builder.append("[Properties]");
        for (Entry<String, Object> entry : properties.entrySet()) {
            builder.append(Strings.LINE_SEPARATOR);
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
        }
        builder.append(Strings.LINE_SEPARATOR);
        builder.append("----------");
        return builder.toString();
    }

    private void initializeConfigurationAwareComponents(
            Set<ConfigurationAware> components) {
        for (ConfigurationAware component : components) {
            component.init(this);
        }
    }

    private List<Resolver> identifyResolvers(MustacheEngineBuilder builder) {
        Set<Resolver> builderResolvers = builder.buildResolvers();
        List<Resolver> resolvers = new ArrayList<Resolver>();
        if (!builderResolvers.isEmpty()) {
            resolvers.addAll(builderResolvers);
            Collections.sort(resolvers, new HighPriorityComparator());
        }
        return resolvers;
    }

    private Map<String, Object> initializeProperties(
            MustacheEngineBuilder engineBuilder,
            Set<ConfigurationAware> initialSet) {

        Set<ConfigurationKey> keysToProcess = getConfigurationKeysToProcess(initialSet);

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        Map<String, Object> builderProperties = engineBuilder.buildProperties();
        Properties resourceProperties = new Properties();

        try {
            InputStream in = this.getClass().getResourceAsStream(RESOURCE_FILE);
            if (in != null) {
                try {
                    resourceProperties.load(in);
                } finally {
                    in.close();
                }
            }
        } catch (IOException e) {
            // No-op, file is optional
        }

        for (ConfigurationKey configKey : keysToProcess) {

            String key = configKey.get();

            // Manually set properties
            Object value = builderProperties.get(key);

            if (value == null) {
                // System properties
                value = SecurityActions.getSystemProperty(key);
                if (value == null) {
                    // Resource properties
                    value = resourceProperties.getProperty(key);
                }
            }

            if (value != null) {
                try {
                    value = ConfigurationProperties.convertConfigValue(
                            configKey.getDefaultValue().getClass(), value);
                } catch (Exception e) {
                    throw new MustacheException(
                            MustacheProblem.CONFIG_PROPERTY_INVALID_VALUE, e);
                }
            } else {
                value = configKey.getDefaultValue();
            }
            builder.put(key, value);
        }
        return builder.build();
    }

    private Set<ConfigurationKey> getConfigurationKeysToProcess(
            Set<ConfigurationAware> components) {
        Set<ConfigurationKey> keys = new HashSet<ConfigurationKey>();
        // Global keys
        for (ConfigurationKey key : EngineConfigurationKey.values()) {
            keys.add(key);
        }
        for (ConfigurationAware component : components) {
            keys.addAll(component.getConfigurationKeys());
        }
        return keys;
    }

    private TextSupport identifyTextSupport(MustacheEngineBuilder builder) {
        return builder.getTextSupport() != null ? builder.getTextSupport()
                : new TextSupportFactory().createTextSupport();
    }

    private LocaleSupport identifyLocaleSupport(MustacheEngineBuilder builder) {
        return builder.getLocaleSupport() != null ? builder.getLocaleSupport()
                : new LocaleSupportFactory().createLocateSupport();
    }

    private KeySplitter identifyKeySplitter(MustacheEngineBuilder builder) {
        // Factory does not make sense here
        return builder.getKeySplitter() != null ? builder.getKeySplitter()
                : new DotKeySplitter();
    }

    private MissingValueHandler identifyMissingValueHandler(
            MustacheEngineBuilder builder) {
        return builder.getMissingValueHandler() != null ? builder
                .getMissingValueHandler() : new NoOpMissingValueHandler();
    }

    private List<TemplateLocator> identifyTemplateLocators(
            MustacheEngineBuilder builder) {
        Set<TemplateLocator> builderTemplateLocators = builder
                .buildTemplateLocators();
        if (!builderTemplateLocators.isEmpty()) {
            List<TemplateLocator> locators = new ArrayList<TemplateLocator>(
                    builder.buildTemplateLocators());
            Collections.sort(locators, new HighPriorityComparator());
            return ImmutableList.copyOf(locators);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private Set<ConfigurationAware> getConfigurationAwareComponents(
            Set<ConfigurationAware> initialSet) {

        Set<ConfigurationAware> components = new HashSet<ConfigurationAware>();

        components.add(missingValueHandler);
        components.addAll(helpers.values());
        components.addAll(resolvers);

        if (templateLocators != null) {
            components.addAll(templateLocators);
        }
        if (mustacheListeners != null) {
            components.addAll(mustacheListeners);
        }
        components.add(localeSupport);
        components.add(textSupport);
        components.add(keySplitter);
        return components;
    }

    private <T> void removeInvalidComponents(Iterable<T> iterable) {
        for (Iterator<T> iterator = iterable.iterator(); iterator.hasNext();) {
            T component = iterator.next();
            if (component instanceof Validateable
                    && !((Validateable) component).isValid()) {
                iterator.remove();
            }
        }
    }

}
