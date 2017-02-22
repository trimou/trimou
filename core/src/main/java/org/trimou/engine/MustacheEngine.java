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

import java.util.function.Predicate;

import org.trimou.Mustache;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.TemplateLocator;

/**
 * Mustache engine is the central point for template management. It has a
 * dedicated {@link Configuration} and template cache. Implementation must be
 * thread-safe.
 *
 * @author Martin Kouba
 */
public interface MustacheEngine {

    /**
     * @see ComputingCache
     */
    String COMPUTING_CACHE_CONSUMER_ID = MustacheEngine.class.getName();

    /**
     * Get the compiled template with the given id.
     * <p>
     * This method involves all template locators to locate the template
     * contents. Locators with higher priority are called first.
     * <p>
     * By default the compiled template is automatically put into the template
     * cache so that no compilation happens the next time the template is
     * requested.
     *
     * @param name
     *            The template identifier
     * @return the compiled template with the given name or <code>null</code> if
     *         no such template exists
     * @see Mustache#getName()
     * @see TemplateLocator
     * @see EngineConfigurationKey#TEMPLATE_CACHE_ENABLED
     * @see EngineConfigurationKey#TEMPLATE_CACHE_EXPIRATION_TIMEOUT
     */
    Mustache getMustache(String name);

    /**
     * @param name
     * @return uncompiled mustache template source.
     * @see Mustache#getName()
     */
    String getMustacheSource(String name);

    /**
     * Compile the given template. The compiled template is not cached and so
     * it's not available for partials and template inheritance.
     *
     * @param name
     *            The template identifier
     * @param templateContent
     * @return the compiled template
     */
    Mustache compileMustache(String name, String templateContent);

    /**
     * @return the engine configuration
     */
    Configuration getConfiguration();

    /**
     * Invalidate all the cache entries for both compiled and uncompiled templates.
     *
     * @see #invalidateTemplateCache(Predicate)
     */
    void invalidateTemplateCache();

    /**
     * Invalidate the cache entries whose template name is matching the given predicate.
     *
     * @param predicate
     * @see #invalidateTemplateCache()
     */
    void invalidateTemplateCache(Predicate<String> predicate);

}
