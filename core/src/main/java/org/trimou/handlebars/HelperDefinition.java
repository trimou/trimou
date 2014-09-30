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
package org.trimou.handlebars;

import java.util.List;
import java.util.Map;

import org.trimou.engine.MustacheTagInfo;

/**
 * A helper definition metadata.
 *
 * @author Martin Kouba
 * @since 1.5
 */
public interface HelperDefinition {

    /**
     *
     * @return the info about the associated tag
     */
    MustacheTagInfo getTagInfo();

    /**
     * The list may contain {@link ValuePlaceholder} instances during
     * validation.
     *
     * @return an immutable list of parameters
     */
    List<Object> getParameters();

    /**
     * The map may contain {@link ValuePlaceholder} instances during validation.
     *
     * @return an immutable "hash" map
     */
    Map<String, Object> getHash();

    /**
     *
     * @return the reconstructed literal block (original
     *         text before compilation) the helper contains, or an empty string for variable tag helpers
     * @since 1.7
     */
    String getContentLiteralBlock();

    /**
     * A value placeholder represents an expression which will be evaluated
     * right before the helper execution. The placeholder is then replaced with
     * the actual value, i.e. placeholders are only present during tag
     * validation.
     */
    public interface ValuePlaceholder {

        public String getName();

    }

}
