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

/**
 * A list of common keys used in {@link Options} hash.
 *
 * @author Martin Kouba
 * @see Options#getHash()
 */
public final class OptionsHashKeys {

    private OptionsHashKeys() {
    }

    public static final String FORMAT = "format";

    public static final String STYLE = "style";

    public static final String LOCALE = "locale";

    public static final String TIME_ZONE = "timeZone";

    public static final String PATTERN = "pattern";

    public static final String DELIMITER = "delimiter";

    public static final String LAMBDA = "lambda";

    public static final String APPLY = "apply";

    public static final String LOGIC = "logic";

    public static final String LEVEL = "level";

    public static final String BASE_NAME = "baseName";

    public static final String ELSE = "else";

    public static final String OPERATOR = "op";

    public static final String OUTPUT = "out";

    public static final String AS = "as";

    public static final String BREAK = "break";

}
