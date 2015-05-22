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

package org.trimou.engine.interpolation;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.config.ConfigurationAware;
import org.trimou.handlebars.Helper;

/**
 * Allows to customize the way the helpers extract literals from params and hash
 * values.
 *
 * @author Martin Kouba
 * @see Helper
 * @since 1.8
 */
public interface LiteralSupport extends ConfigurationAware {

    /**
     *
     * @param value
     * @param tagInfo
     * @return the literal instance for the given value or <code>null</code> if
     *         the value does not represent any supported literal type
     */
    Object getLiteral(String value, MustacheTagInfo tagInfo);

}
