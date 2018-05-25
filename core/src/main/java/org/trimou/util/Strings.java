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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Strings {

    public static final String EMPTY = "";

    public static final String GAP = " ";

    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /**
     * Linux, BDS, etc.
     */
    public static final String LINE_SEPARATOR_LF = "\n";

    /**
     * DOS, OS/2, Microsoft Windows, etc.
     */
    public static final String LINE_SEPARATOR_CRLF = "\r\n";

    /**
     * Mac OS 9, ZX Spectrum :-), etc.
     */
    public static final String LINE_SEPARATOR_CR = "\r";

    public static final String SLASH = "/";

    public static final String FILE_SEPARATOR = System
            .getProperty("file.separator");

    public static final String DOT = ".";

    public static final String UNDERSCORE = "_";

    public static final String NOT_AVAILABLE = "N/A";

    public static final String URL_PROCOTOL_FILE = "file";

    public static final String THIS = "this";

    public static final String HASH = "#";

    private Strings() {
    }

    /**
     *
     * @param character
     * @return <code>true</code> if the char is a string literal separator,
     *         <code>false</code> otherwise
     */
    public static boolean isStringLiteralSeparator(char character) {
        return character == '"' || character == '\'';
    }

    /**
     *
     * @param sequence
     * @return <code>true</code> if the given sequence is null or empty
     */
    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    /**
     *
     * @param word
     * @return the uncapitalized input
     */
    public static String uncapitalize(String word) {
        if (isEmpty(word)) {
            return word;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(word.charAt(0)));
        if (word.length() > 1) {
            builder.append(word.substring(1, word.length()));
        }
        return builder.toString();
    }

    /**
     * Capitalizes all the delimiter separated words.
     *
     * @param text
     * @param delimiter
     * @return the capitalized input
     */
    public static String capitalizeFully(String text, Character delimiter) {
        if (isEmpty(text)) {
            return text;
        }
        text = text.toLowerCase();
        boolean capitalizeNext = true;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char ch = text.charAt(i);
            if (delimiter.equals(ch)) {
                capitalizeNext = true;
                builder.append(ch);
            } else if (capitalizeNext) {
                builder.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    /**
     *
     * @param text
     * @param search
     * @param replacement
     * @return the processed text
     */
    public static String replace(String text, String search,
            String replacement) {
        if (isEmpty(text) || isEmpty(search) || replacement == null) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(search, start);
        if (end == -1) {
            return text;
        }
        StringBuilder builder = new StringBuilder();
        while (end != -1) {
            builder.append(text.substring(start, end));
            builder.append(replacement);
            start = end + search.length();
            end = text.indexOf(search, start);
        }
        builder.append(text.substring(start));
        return builder.toString();
    }

    /**
     *
     * @param text
     * @param suffix
     * @return the text without the suffix, if the text ends with the suffix
     */
    public static String removeSuffix(String text, String suffix) {
        if (isEmpty(text) || isEmpty(suffix)) {
            return text;
        }
        if (text.endsWith(suffix)) {
            return text.substring(0, text.length() - suffix.length());
        }
        return text;
    }

    /**
     *
     * @param text
     * @param delimiter
     * @return a list of tokens
     */
    public static List<String> split(String text, String delimiter) {
        if (isEmpty(text) || isEmpty(delimiter)) {
            return Collections.singletonList(text);
        }
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter);
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return Collections.unmodifiableList(tokens);
    }

    /**
     *
     * @param text
     * @return <code>true</code> if the given text contains a whitespace
     *         character
     */
    public static boolean containsWhitespace(String text) {
        if (isEmpty(text)) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param text
     * @return <code>true</code> if the given text contains only whitespace
     *         characters
     */
    public static boolean containsOnlyWhitespace(String text) {
        if (isEmpty(text)) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param text
     * @return <code>true</code> if the given text contains only digits
     */
    public static boolean containsOnlyDigits(String text) {
        if (isEmpty(text)) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param text
     * @return a new String consisting of the input repeated the specified
     *         number of times
     */
    public static String repeat(String text, int times, String separator) {
        if (isEmpty(text) || times < 0) {
            return text;
        }
        if (times == 0) {
            return EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(text);
            if (i + 1 < times) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    /**
     *
     * @param text
     * @param delimiter
     * @return the substring after the first delimiter or an empty string if
     *         nothing is found
     */
    public static String substringAfter(String text, String delimiter) {
        Checker.checkArgumentsNotNull(text, delimiter);
        int index = text.indexOf(delimiter);
        if (index == -1) {
            return EMPTY;
        }
        return text.substring(index + delimiter.length());
    }

    public static boolean isListLiteralStart(char character) {
        return character == '[';
    }

    public static boolean isListLiteralEnd(char character) {
        return character == ']';
    }

    public static boolean isListLiteral(String value) {
        return isListLiteralStart(value.charAt(0)) && isListLiteralEnd(value.charAt(value.length() - 1));
    }

}
