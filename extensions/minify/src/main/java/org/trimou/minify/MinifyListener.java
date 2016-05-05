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
package org.trimou.minify;

import static org.trimou.util.Checker.checkArgumentNotNull;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.listener.AbstractMustacheListener;
import org.trimou.engine.listener.MustacheParsingEvent;

/**
 *
 * @author Martin Kouba
 */
public class MinifyListener extends AbstractMustacheListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MinifyListener.class);

    private final Minifier minifier;

    MinifyListener(Minifier minifier) {
        checkArgumentNotNull(minifier);
        this.minifier = minifier;
    }

    @Override
    public void init(Configuration configuration) {
        minifier.init(configuration);
        LOGGER.info("Minify listener initialized [minifier: {}]",
                minifier.toString());
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return minifier.getConfigurationKeys();
    }

    @Override
    public void parsingStarted(MustacheParsingEvent event) {
        event.setMustacheContents(minifier.minify(event.getMustacheName(),
                event.getMustacheContents()));
    }

}
