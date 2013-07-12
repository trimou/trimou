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

import java.util.List;
import java.util.Map;

import org.trimou.Mustache;
import org.trimou.annotations.Internal;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.text.TextSupport;

/**
 * Mustache engine configuration.
 *
 * @author Martin Kouba
 */
@Internal
public interface Configuration {

    /**
     * @return the ordered immutable list of resolvers, or <code>null</code> if
     *         no resolvers defined
     */
    public List<Resolver> getResolvers();

    /**
     * @return the immutable map of global values, or <code>null</code> if no
     *         global values defined
     */
    public Map<String, Object> getGlobalData();

    /**
     * @return the ordered immutable list of template locators, or
     *         <code>null</code> if no template locators defined
     */
    public List<TemplateLocator> getTemplateLocators();

    /**
     * @return the text support implementation
     */
    public TextSupport getTextSupport();

    /**
     * @return the locale support implementation
     */
    public LocaleSupport getLocaleSupport();

    /**
     * @return the immutable list of {@link Mustache} listeners
     */
    public List<MustacheListener> getMustacheListeners();

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    public Long getLongPropertyValue(ConfigurationKey configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    public Integer getIntegerPropertyValue(ConfigurationKey configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    public String getStringPropertyValue(ConfigurationKey configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    public Boolean getBooleanPropertyValue(ConfigurationKey configurationKey);

}
