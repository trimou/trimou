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
package org.trimou.engine;

/**
 *
 * @author Martin Kouba
 */
public enum MustacheTagType {

    VARIABLE(null),
    UNESCAPE_VARIABLE('&'),
    SECTION('#'),
    INVERTED_SECTION('^'),
    SECTION_END('/'),
    COMMENT('!'),
    PARTIAL('>'),
    DELIMITER('='),
    EXTEND('<'),
    EXTEND_SECTION('$');

    MustacheTagType(Character command) {
        this.command = command;
    }

    private Character command;

    public Character getCommand() {
        return command;
    }

    public static boolean contentMustBeNonWhitespaceCharacterSequence(
            MustacheTagType type) {
        return VARIABLE.equals(type) || UNESCAPE_VARIABLE.equals(type)
                || SECTION.equals(type) || INVERTED_SECTION.equals(type)
                || SECTION_END.equals(type) || PARTIAL.equals(type)
                || EXTEND.equals(type) || EXTEND_SECTION.equals(type);
    }

}
