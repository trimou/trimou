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
package org.trimou.engine.locator;

import java.io.Reader;
import java.util.Set;

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.priority.WithPriority;

/**
 * Automatically locates the template contents for the given template
 * identifier. The form of the template identifier is not defined, however in
 * most cases the id will represent a template name or virtual path.
 *
 * Implementation must be thread-safe.
 *
 * @author Martin Kouba
 */
public interface TemplateLocator extends WithPriority, ConfigurationAware {

    int DEFAULT_PRIORITY = 10;

    /**
     * The reader is always closed by the engine right after the template source is read.
     *
     * @param name
     *            The template identifier
     * @return the reader object for a template with the given name or
     *         <code>null</code> if no available template with the given name
     *         exists
     * @see org.trimou.Mustache#getName()
     */
    public Reader locate(String name);

    /**
     *
     * @return the set of names of all available template identifiers (i.e. all
     *         available templates)
     * @see EngineConfigurationKey#PRECOMPILE_ALL_TEMPLATES
     */
    public Set<String> getAllIdentifiers();

    @Override
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

}
