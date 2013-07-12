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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;

import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locale.LocaleSupportFactory;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.priority.HighPriorityComparator;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.text.TextSupport;
import org.trimou.engine.text.TextSupportFactory;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
class DefaultConfiguration implements Configuration {

    private static final String RESOURCE_FILE = "/trimou.properties";

    private List<TemplateLocator> templateLocators = null;

    private List<Resolver> resolvers = null;

    private Map<String, Object> globalData = null;

    private TextSupport textSupport;

    private LocaleSupport localeSupport;

    private Map<String, Object> properties;

    private List<MustacheListener> mustacheListeners;

    /**
     *
     * @param builder
     */
    DefaultConfiguration(MustacheEngineBuilder builder) {

        if (!builder.isOmitServiceLoaderConfigurationExtensions()) {
            // Process configuration extensions
            for (Iterator<ConfigurationExtension> iterator = ServiceLoader
                    .load(ConfigurationExtension.class).iterator(); iterator
                    .hasNext();) {
                iterator.next().register(builder);
            }
        }
        identifyResolvers(builder);
        identifyTextSupport(builder);
        identifyLocaleSupport(builder);
        identifyTemplateLocators(builder);
        identifyMustacheListeners(builder);
        initializeGlobalData(builder);
        initializeProperties(builder);
        initializeConfigurationAwareComponents();
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
    public Long getLongPropertyValue(ConfigurationKey configurationKey) {

        Long value = (Long) properties.get(configurationKey.get());

        if (value == null) {
            value = (Long) configurationKey.getDefaultValue();
        }
        return value;
    }

    @Override
    public Integer getIntegerPropertyValue(ConfigurationKey configurationKey) {

        Integer value = (Integer) properties.get(configurationKey.get());

        if (value == null) {
            value = (Integer) configurationKey.getDefaultValue();
        }
        return value;
    }

    @Override
    public String getStringPropertyValue(ConfigurationKey configurationKey) {

        Object value = properties.get(configurationKey.get());

        if (value == null) {
            value = configurationKey.getDefaultValue();
        }
        return value.toString();
    }

    @Override
    public Boolean getBooleanPropertyValue(ConfigurationKey configurationKey) {

        Boolean value = (Boolean) properties.get(configurationKey.get());

        if (value == null) {
            value = (Boolean) configurationKey.getDefaultValue();
        }
        return value;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("----------");
        builder.append(Strings.LINE_SEPARATOR);
        builder.append("[");
        builder.append(this.getClass().getName());
        builder.append("]");
        if (templateLocators != null) {
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("----------");
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("[Template locators]");
            for (TemplateLocator locator : templateLocators) {
                builder.append(Strings.LINE_SEPARATOR);
                builder.append(locator.toString());
            }
        }
        if (resolvers != null) {
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("----------");
            builder.append(Strings.LINE_SEPARATOR);
            builder.append("[Resolvers]");
            for (Resolver resolver : resolvers) {
                builder.append(Strings.LINE_SEPARATOR);
                builder.append(resolver.toString());
            }
        }
        builder.append(Strings.LINE_SEPARATOR);
        builder.append("----------");
        builder.append(Strings.LINE_SEPARATOR);
        builder.append("[Properties]");
        for (Entry<String, Object> entry : properties.entrySet()) {
            builder.append(Strings.LINE_SEPARATOR);
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
        }
        return builder.toString();
    }

    private Object getPropertyValue(Object defaultValue, Object suppliedValue) {

        if (defaultValue instanceof String) {
            return suppliedValue.toString();
        } else if (defaultValue instanceof Boolean) {
            return Boolean.valueOf(suppliedValue.toString());
        } else if (defaultValue instanceof Long) {
            return Long.valueOf(suppliedValue.toString());
        } else if (defaultValue instanceof Integer) {
            return Integer.valueOf(suppliedValue.toString());
        }
        throw new IllegalStateException("Unknown configuration value");
    }

    private void initializeConfigurationAwareComponents() {
        for (ConfigurationAware component : getConfigurationAwareComponents()) {
            component.init(this);
        }
    }

    private void identifyResolvers(MustacheEngineBuilder builder) {
        resolvers = new ArrayList<Resolver>();
        if (builder.getResolvers() != null) {
            resolvers.addAll(builder.getResolvers());
        }
        Collections.sort(resolvers, new HighPriorityComparator());
        resolvers = ImmutableList.copyOf(resolvers);
    }

    private void initializeProperties(MustacheEngineBuilder builder) {

        Set<ConfigurationKey> keysToProcess = getConfigurationKeysToProcess();

        properties = new HashMap<String, Object>(keysToProcess.size());
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
            Object value = builder.getProperties().get(key);

            if (value == null) {
                // System properties
                value = System.getProperty(key);
                if (value == null) {
                    // Resource properties
                    value = resourceProperties.getProperty(key);
                }
            }
            properties.put(
                    key,
                    value != null ? getPropertyValue(
                            configKey.getDefaultValue(), value) : configKey
                            .getDefaultValue());
        }
    }

    private Set<ConfigurationKey> getConfigurationKeysToProcess() {
        Set<ConfigurationKey> keys = new HashSet<ConfigurationKey>();
        // Global keys
        for (ConfigurationKey key : EngineConfigurationKey.values()) {
            keys.add(key);
        }
        for (ConfigurationAware component : getConfigurationAwareComponents()) {
            keys.addAll(component.getConfigurationKeys());
        }
        return keys;
    }

    private void identifyTextSupport(MustacheEngineBuilder builder) {
        if (builder.getTextSupport() != null) {
            textSupport = builder.getTextSupport();
        } else {
            textSupport = new TextSupportFactory().createTextSupport();
        }
    }

    private void identifyLocaleSupport(MustacheEngineBuilder builder) {
        if (builder.getLocaleSupport() != null) {
            localeSupport = builder.getLocaleSupport();
        } else {
            localeSupport = new LocaleSupportFactory().createLocateSupport();
        }
    }

    private void identifyTemplateLocators(MustacheEngineBuilder builder) {
        if (builder.getTemplateLocators() != null) {
            List<TemplateLocator> locators = new ArrayList<TemplateLocator>(
                    builder.getTemplateLocators());
            Collections.sort(locators, new HighPriorityComparator());
            this.templateLocators = ImmutableList.copyOf(locators);
        }
    }

    private void initializeGlobalData(MustacheEngineBuilder builder) {
        if (builder.getGlobalData() != null) {
            this.globalData = ImmutableMap.copyOf(builder.getGlobalData());
        }
    }

    private void identifyMustacheListeners(MustacheEngineBuilder builder) {
        if (builder.getMustacheListeners() != null) {
            this.mustacheListeners = ImmutableList.copyOf(builder
                    .getMustacheListeners());
        }
    }

    private Set<ConfigurationAware> getConfigurationAwareComponents() {
        Set<ConfigurationAware> components = new HashSet<ConfigurationAware>();
        components.addAll(resolvers);
        if (templateLocators != null) {
            components.addAll(templateLocators);
        }
        if (mustacheListeners != null) {
            components.addAll(mustacheListeners);
        }
        components.add(localeSupport);
        components.add(textSupport);
        return components;
    }

}
