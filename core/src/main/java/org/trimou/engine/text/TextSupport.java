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
package org.trimou.engine.text;

import java.io.IOException;

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.util.Checker;

/**
 * Text support. Implementation must be thread-safe.
 *
 * @author Martin Kouba
 */
public interface TextSupport extends ConfigurationAware {

    /**
     * Interpolated values should be HTML-escaped, if appropriate.
     * <p>
     * Clients are encouraged to use
     * {@link #appendEscapedHtml(String, Appendable)} instead (this method
     * might be optimized to consume less resources).
     *
     * @param input
     * @return the escaped text
     * @see #appendEscapedHtml(String, Appendable)
     */
    String escapeHtml(String input);

    /**
     * Escape the input and append the result to the given appendable.
     * Interpolated values should be HTML-escaped, if appropriate.
     *
     * @param input
     * @param appendable
     * @throws IOException
     * @since 2.0
     */
    default void appendEscapedHtml(String input, Appendable appendable)
            throws IOException {
        Checker.checkArgumentNotNull(appendable);
        appendable.append(escapeHtml(input));
    }

}
