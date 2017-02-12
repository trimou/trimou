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
package org.trimou.engine.parser;

import org.trimou.Mustache;
import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;
import org.trimou.exception.MustacheException;

/**
 * Handler for parsing events.
 */
@Internal
public interface ParsingHandler {

    /**
     * Parsing started.
     *
     * @param name
     * @param delimiters
     */
    void startTemplate(String name, Delimiters delimiters,
            MustacheEngine engine);

    /**
     * Flush a text segment.
     *
     * @param text
     */
    void text(String text);

    /**
     * Flush a tag.
     *
     * @param tag
     */
    void tag(ParsedTag tag);

    /**
     * Flush a line separator.
     *
     * @param separator
     */
    void lineSeparator(String separator);

    /**
     * Parsing ended.
     */
    void endTemplate();

    /**
     *
     * @return the compiled template
     * @throws MustacheException
     *             If not finished yet
     */
    Mustache getCompiledTemplate();

}
