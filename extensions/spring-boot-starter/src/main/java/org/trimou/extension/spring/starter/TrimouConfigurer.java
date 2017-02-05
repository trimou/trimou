/*
 * Copyright 2017 Trimou Team
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

package org.trimou.extension.spring.starter;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.Resolver;
import org.trimou.handlebars.Helper;
import org.trimou.lambda.Lambda;

/**
 * Callback interface that can be implemented by beans wishing to customize the Trimou template engine
 * {@link MustacheEngine}  before it is used by an auto-configured instance.
 * <p>
 * To register a {@link Helper}, {@link Lambda} or a {@link Resolver} implement this interface and modify the
 * {@link MustacheEngineBuilder} directly before it gets built.
 * <p>
 * <pre>
 * {@code
 *  &#064;Configuration
 *  class TrimouConfigurationDecorator implements TrimouConfigurer {
 *
 *      &#064;Override
 *      public void configure(final MustacheEngineBuilder engineBuilder) {
 *          engineBuilder.addGlobalData("footer", "(c) Trimou Team");
 *      }
 *  }
 * }
 * </pre>
 */
public interface TrimouConfigurer {

    /**
     * Customize the settings or properties before they get applied to the {@link MustacheEngineBuilder}.
     *
     * @param engineBuilder the builder used to create the {@link MustacheEngine} instance
     */
    void configure(MustacheEngineBuilder engineBuilder);
}
