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

import java.io.Reader;

import org.trimou.engine.MustacheEngineBuilder.EngineBuiltCallback;
import org.trimou.engine.config.ConfigurationAware;

/**
 * Although the minifier is a configuration-aware component, it's allowed to
 * define its own configuration keys only if used as a part of a
 * {@link MinifyListener} or registered as an {@link EngineBuiltCallback} if
 * used as a part of a {@link MinifyLambda}.
 *
 * @author Martin Kouba
 */
public interface Minifier extends ConfigurationAware {

    /**
     * Minify the template contents.
     *
     * @param mustacheName
     * @param mustacheContents
     * @return the minified template contents
     */
    Reader minify(String mustacheName, Reader mustacheContents);

    /**
     * Minify the specified text (aka part of the template contents).
     *
     * @param text
     * @return the minified text
     */
    String minify(String text);

}
