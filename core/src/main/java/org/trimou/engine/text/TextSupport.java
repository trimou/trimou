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

import org.trimou.engine.config.ConfigurationAware;

/**
 * Text support. Implementation must be thread-safe.
 *
 * @author Martin Kouba
 */
public interface TextSupport extends ConfigurationAware {

    /**
     * Interpolated values should be HTML escaped, if appropriate.
     *
     * @param input
     * @return escaped text
     */
    public String escapeHtml(String input);

}
