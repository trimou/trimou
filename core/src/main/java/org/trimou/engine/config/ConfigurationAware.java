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

import java.util.Collections;
import java.util.Set;

/**
 * Configuration-aware components can define additional configuration keys to be
 * processed and also initialize themselves with partially initialized engine
 * configuration.
 *
 * @author Martin Kouba
 */
public interface ConfigurationAware {

    /**
     * Initialize the component. Keep in mind that the configuration itself
     * might not be fully initialized yet. However it should be safe to inspect
     * non-configurable components (e.g. properties and global data) and obtain
     * references to other {@link ConfigurationAware} components e.g. (
     * {@link org.trimou.engine.locale.LocaleSupport}).
     *
     * This method must not be called directly by the application.
     *
     * @param configuration
     */
    default void init(Configuration configuration) {
        // No-op by default
    }

    /**
     *
     * @return the set of configuration keys to discover
     */
    default Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.emptySet();
    }

}
