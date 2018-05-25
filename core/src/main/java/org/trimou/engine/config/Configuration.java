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
import java.util.concurrent.ExecutorService;

import org.trimou.Mustache;
import org.trimou.engine.cache.ComputingCacheFactory;
import org.trimou.engine.convert.ContextConverter;
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
import org.trimou.handlebars.Options;

/**
 * Mustache engine configuration.
 *
 * @author Martin Kouba
 */
public interface Configuration {

    /**
     * @return the ordered immutable list of resolvers, or <code>null</code> if no
     *         resolvers defined
     */
    List<Resolver> getResolvers();

    /**
     * @return the immutable map of global values, or <code>null</code> if no global
     *         values defined
     */
    Map<String, Object> getGlobalData();

    /**
     * @return the ordered immutable list of template locators, or <code>null</code>
     *         if no template locators defined
     */
    List<TemplateLocator> getTemplateLocators();

    /**
     * @return the text support implementation
     */
    TextSupport getTextSupport();

    /**
     * @return the locale support implementation
     */
    LocaleSupport getLocaleSupport();

    /**
     * @return the immutable list of {@link Mustache} listeners
     */
    List<MustacheListener> getMustacheListeners();

    /**
     * @return the key splitter implementation
     * @since 1.5
     */
    KeySplitter getKeySplitter();

    /**
     * @return the missing value handler implementation
     * @since 1.5
     */
    MissingValueHandler getMissingValueHandler();

    /**
     * @return the immutable map of registered helpers
     */
    Map<String, Helper> getHelpers();

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    <T extends ConfigurationKey> Long getLongPropertyValue(T configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    <T extends ConfigurationKey> Integer getIntegerPropertyValue(T configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    <T extends ConfigurationKey> String getStringPropertyValue(T configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     * @since 2.4
     */
    <T extends ConfigurationKey> Object getPropertyValue(T configurationKey);

    /**
     *
     * @param configurationKey
     * @return the property value for the given key
     */
    <T extends ConfigurationKey> Boolean getBooleanPropertyValue(T configurationKey);

    /**
     * @return the description info
     */
    String getInfo();

    /**
     *
     * @return the computing cache factory
     * @since 1.7
     */
    ComputingCacheFactory getComputingCacheFactory();

    /**
     *
     * @return the idenfitier generator
     * @since 1.7
     */
    IdentifierGenerator getIdentifierGenerator();

    /**
     *
     * @return the executor service to be used for async tasks
     * @see Options#executeAsync(org.trimou.handlebars.Options.HelperExecutable)
     * @since 1.8
     */
    ExecutorService geExecutorService();

    /**
     *
     * @return the literal support implementation
     * @since 1.8
     */
    LiteralSupport getLiteralSupport();

    /**
     *
     * @return the immutable list of converters
     * @since 2.1
     */
    List<ValueConverter> getValueConverters();

    /**
    *
    * @return the immutable list of converters
    * @since 2.5
    */
   List<ContextConverter> getContextConverters();
}
