/*
 * Copyright 2015 Martin Kouba
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
package org.trimou.jdk8.handlebars;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.handlebars.AbstractHelper;
import org.trimou.handlebars.Helper;
import org.trimou.handlebars.HelperDefinition;
import org.trimou.handlebars.Options;
import org.trimou.util.Checker;

/**
 * Allows to create simple helpers using JDK8 funcional interfaces.
 *
 * @author Martin Kouba
 */
public final class SimpleHelpers {

    private SimpleHelpers() {
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @param executionCallback
     * @return a simple helper instance (no validation, no configuration)
     */
    public static Helper execute(
            BiConsumer<Options, Configuration> executionCallback) {
        return builder().execute(executionCallback).build();
    }

    /**
     * The builder is not thread-safe and should not be reused.
     *
     * @author Martin Kouba
     * @see Helper#execute(Options)
     * @see Helper#validate(HelperDefinition)
     */
    public static class Builder {

        private Set<ConfigurationKey> configurationKeys;

        private BiConsumer<Options, Configuration> executionCallback;

        private BiConsumer<HelperDefinition, Configuration> validationCallback;

        public Builder execute(
                BiConsumer<Options, Configuration> executionCallback) {
            this.executionCallback = executionCallback;
            return this;
        }

        public Builder validate(
                BiConsumer<HelperDefinition, Configuration> validationCallback) {
            this.validationCallback = validationCallback;
            return this;
        }

        public Builder addConfigurationKey(ConfigurationKey key) {
            if (configurationKeys == null) {
                configurationKeys = new HashSet<>();
            }
            this.configurationKeys.add(key);
            return this;
        }

        public Helper build() {
            return new SimpleHelper(configurationKeys, executionCallback,
                    validationCallback);
        }

    }

    static class SimpleHelper extends AbstractHelper {

        private final Set<ConfigurationKey> configurationKeys;

        private final BiConsumer<Options, Configuration> executionCallback;

        private final BiConsumer<HelperDefinition, Configuration> validationCallback;

        /**
         *
         * @param configurationKeys
         * @param executionCallback
         * @param validationCallback
         */
        private SimpleHelper(Set<ConfigurationKey> configurationKeys,
                BiConsumer<Options, Configuration> executionCallback,
                BiConsumer<HelperDefinition, Configuration> validationCallback) {
            Checker.checkArgumentNotNull(executionCallback);
            this.configurationKeys = configurationKeys == null
                    ? Collections.emptySet() : configurationKeys;
            this.executionCallback = executionCallback;
            this.validationCallback = validationCallback;
        }

        @Override
        public void execute(Options options) {
            executionCallback.accept(options, configuration);
        }

        @Override
        public void validate(HelperDefinition definition) {
            if (validationCallback != null) {
                validationCallback.accept(definition, configuration);
            }
        }

        @Override
        public Set<ConfigurationKey> getConfigurationKeys() {
            return configurationKeys;
        }

    }

}
