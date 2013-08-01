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
package org.trimou.engine.listener;

import org.trimou.engine.config.AbstractConfigurationAware;

/**
 * Abstract no-op mustache listener.
 *
 * @author Martin Kouba
 */
public abstract class AbstractMustacheListener extends
        AbstractConfigurationAware implements MustacheListener {

    @Override
    public void compilationFinished(MustacheCompilationEvent event) {
        // No-op
    }

    @Override
    public void renderingStarted(MustacheRenderingEvent event) {
        // No-op
    }

    @Override
    public void renderingFinished(MustacheRenderingEvent event) {
        // No-op
    }

    @Override
    public void parsingStarted(MustacheParsingEvent event) {
        // No-op
    }

}
