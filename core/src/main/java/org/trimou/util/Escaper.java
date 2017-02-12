/*
 * Copyright 2016 Martin Kouba
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.trimou.annotations.Internal;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Escapes the characters using a map of replacements.
 *
 * @author Martin Kouba
 */
@Internal
public class Escaper {

    private final Map<Character, String> replacements;

    /**
     *
     * @param replacements
     */
    private Escaper(Map<Character, String> replacements) {
        this.replacements = replacements.isEmpty() ? Collections.emptyMap()
                : new HashMap<>(replacements);
    }

    /**
     *
     * @param value
     * @return an escaped value
     */
    public String escape(String value) {
        Checker.checkArgumentNotNull(value);
        if (value.isEmpty()) {
            return value;
        }
        for (int i = 0; i < value.length(); i++) {
            String replacement = replacements.get(value.charAt(i));
            if (replacement != null) {
                StringBuilder result = new StringBuilder();
                result.append(value.substring(0, i));
                result.append(replacement);
                try {
                    escapeNext(value, i, result);
                } catch (IOException e) {
                    throw new MustacheException(MustacheProblem.RENDER_IO_ERROR,
                            e);
                }
                return result.toString();
            }
        }
        return value;
    }

    /**
     *
     * @param value
     * @param appendable
     * @throws IOException
     */
    public void escape(String value, Appendable appendable) throws IOException {
        Checker.checkArgumentsNotNull(value, appendable);
        if (value.isEmpty()) {
            return;
        }
        for (int i = 0; i < value.length(); i++) {
            String replacement = replacements.get(value.charAt(i));
            if (replacement != null) {
                appendable.append(value.substring(0, i));
                appendable.append(replacement);
                escapeNext(value, i, appendable);
                return;
            }
        }
        appendable.append(value);
    }

    private void escapeNext(String value, int index, Appendable appendable)
            throws IOException {
        int length = value.length();
        while (++index < length) {
            char c = value.charAt(index);
            String replacement = replacements.get(c);
            if (replacement != null) {
                appendable.append(replacement);
            } else {
                appendable.append(c);
            }
        }
    }

    /**
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<Character, String> replacements;

        private Builder() {
            this.replacements = new HashMap<>();
        }

        public Builder add(char c, String replacement) {
            replacements.put(c, replacement);
            return this;
        }

        public Escaper build() {
            return new Escaper(replacements);
        }

    }

}
