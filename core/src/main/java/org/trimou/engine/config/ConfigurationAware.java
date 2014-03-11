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

import java.util.Set;

/**
 * Configuration-aware components can define additional configuration keys to be
 * processed (i.e. system properties) and also initialize self with partially
 * initialized engine configuration (it's only safe to inspect properties and
 * global data).
 *
 * @author Martin Kouba
 */
public interface ConfigurationAware {

    /**
     * Initialize the component. Keep in mind that the configuration itself is
     * likely not fully initialized yet. It's only safe to inspect properties
     * and global data.
     *
     * This method must not be called directly by the application.
     *
     * @param configuration
     */
    public void init(Configuration configuration);

    /**
     *
     * @return the set of configuration keys to discover
     */
    public Set<ConfigurationKey> getConfigurationKeys();

}
