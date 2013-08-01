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
package org.trimou.engine.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.trimou.annotations.Internal;
import org.trimou.util.Strings;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class ConfigurationProperties {

    private ConfigurationProperties() {
    }

    /**
     * Build the property key for the given name and prefix parts. The name is
     * converted to CamelCase (underscore is used as a delimiter):
     *
     * <pre>
     * MY_PROPERTY_NAME
     * </pre>
     *
     * becomes
     *
     * <pre>
     * myPropertyName
     * </pre>
     *
     * @param propertyName
     * @param prefixParts
     * @return the key
     */
    public static String buildPropertyKey(String propertyName,
            String[] prefixParts) {
        return buildPropertyKey(propertyName, Strings.UNDERSCORE, prefixParts);
    }

    /**
     * Build the property key for the given name and prefix parts. The name is
     * converted to CamelCase (using the specified delimiter; delimiters are
     * removed in the end).
     *
     * @param propertyName
     * @param delimiter
     * @param prefixParts
     * @return
     */
    public static String buildPropertyKey(String propertyName,
            String delimiter, String[] prefixParts) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < prefixParts.length; i++) {
            key.append(prefixParts[i]);
            key.append(Strings.DOT);
        }
        key.append(WordUtils.uncapitalize(StringUtils.replace(WordUtils
                .capitalizeFully(propertyName, delimiter.toCharArray()),
                delimiter, "")));
        return key.toString();
    }

}
