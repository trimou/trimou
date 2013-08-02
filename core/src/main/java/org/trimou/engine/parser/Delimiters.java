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

import org.trimou.engine.config.EngineConfigurationKey;

/**
 *
 * @author Martin Kouba
 */
class Delimiters {

    private String start;
    private String end;

    Delimiters(String startDelimiter, String endDelimiter) {
        reset(startDelimiter, endDelimiter);
    }

    protected void setNewValues(String startDelimiter, String endDelimiter) {
        reset(startDelimiter, endDelimiter);
    }

    private void reset(String startDelimiter, String endDelimiter) {
        start = startDelimiter;
        end = endDelimiter;
    }

    public char getStart(int index) {
        return start.charAt(index);
    }

    public boolean isStartOver(int index) {
        return index == (start.length() - 1);
    }

    public String getStartPart(int index) {
        return start.substring(0, index);
    }

    public char getEnd(int index) {
        return end.charAt(index);
    }

    public boolean isEndOver(int index) {
        return index == (end.length() - 1);
    }

    public String getEndPart(int index) {
        return end.substring(0, index);
    }

    public boolean hasDefaultDelimitersSet() {
        return EngineConfigurationKey.START_DELIMITER.getDefaultValue().equals(
                start)
                && EngineConfigurationKey.END_DELIMITER.getDefaultValue()
                        .equals(end);
    }
}
