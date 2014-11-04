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
package org.trimou.util;

import java.util.regex.Pattern;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;

/**
 *
 * @author Martin Kouba
 */
public final class Patterns {

    private Patterns() {
    }

    /**
     * Delimiters are quoted to avoid regexp reserved characters conflict.
     *
     * @param configuration
     * @return the new delimiters pattern
     */
    public static Pattern newMustacheTagPattern(Configuration configuration) {
        StringBuilder regex = new StringBuilder();
        regex.append(Pattern.quote(configuration
                .getStringPropertyValue(EngineConfigurationKey.START_DELIMITER)));
        regex.append(".*?");
        regex.append(Pattern.quote(configuration
                .getStringPropertyValue(EngineConfigurationKey.END_DELIMITER)));
        return Pattern.compile(regex.toString());
    }

    /**
     * Useful to extract start and end delimiters from "set delimiters" tag
     * content, e.g. <code>&lt;% %&gt;</code> (without equals signs).
     *
     * First and third groups contain one or more non-whitespace characters.
     * Second group contains one or more whitespace characters.
     *
     * Originally we used the following pattern:
     * <code>([[^=]&amp;&amp;\\S]+)(\\s+)([[^=]&amp;&amp;\\S]+)</code>.
     *
     * @return the pattern to match new delimiters in "set delimiters" tag
     */
    public static Pattern newSetDelimitersContentPattern() {
        return Pattern.compile("(\\S+)(\\s+)(\\S+)");
    }

    /**
     * @return the pattern to validate a helper name
     */
    public static Pattern newHelperNameValidationPattern() {
        return Pattern.compile("([\\p{L}\\p{Nd}\\p{P}\\p{S}=]+[ ]*)+?");
    }

    /**
     * @return the pattern to macth a string literal in a helper name
     */
    public static Pattern newHelperStringLiteralPattern() {
        return Pattern.compile("(\")(.*)(\")");
    }

}
