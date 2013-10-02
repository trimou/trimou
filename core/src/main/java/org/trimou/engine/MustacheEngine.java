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

import org.trimou.Mustache;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.locator.TemplateLocator;

/**
 * Mustache engine.
 *
 * @author Martin Kouba
 */
public interface MustacheEngine {

    /**
     * Get the compiled template with the given name.
     *
     * <ol>
     * <li>Try to get the template from the template cache</li>
     * <li>If not found, try to locate its contents, compile it and put into the
     * template cache</li>
     * </ol>
     *
     * @param templateId
     *            The template identifier
     * @return the compiled template with the given name or <code>null</code> if
     *         no such template exists
     * @see TemplateLocator
     */
    public Mustache getMustache(String templateId);

    /**
     * Compile the given template. The compiled template is not cached and so
     * it's not available for partials and template inheritance.
     *
     * @param templateId
     *            The template identifier
     * @param templateContent
     * @return the compiled template
     */
    public Mustache compileMustache(String templateId, String templateContent);

    /**
     * @return the engine configuration
     */
    public Configuration getConfiguration();

    /**
     * Invalidate the template cache.
     */
    public void invalidateTemplateCache();

}
