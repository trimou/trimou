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

/**
 * Abstract configuration-aware component that holds a reference to the
 * {@link Configuration} instance.
 *
 * @author Martin Kouba
 */
public abstract class AbstractConfigurationAware implements ConfigurationAware {

    protected volatile Configuration configuration;

    /**
     * When overriding this method, always call <code>super.init(config)</code>.
     */
    @Override
    public void init(Configuration configuration) {
        checkNotInitialized(this.configuration != null);
        this.configuration = configuration;
        this.init();
    }

    /**
     * Can be overridden so that there's no need to call
     * <code>super.init(Configuration)</code>.
     */
    protected void init() {
    }

    /**
     * @throws IllegalStateException
     *             If the isInitializedExpression evaluates to true
     */
    protected void checkNotInitialized(boolean isInitializedExpression) {
        if (isInitializedExpression) {
            throw new IllegalStateException(
                    "Component is already initialized!");
        }
    }

}
