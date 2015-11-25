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

import static org.trimou.engine.config.EngineConfigurationKey.END_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.START_DELIMITER;
import static org.trimou.util.Checker.checkArgumentNotEmpty;
import static org.trimou.util.Checker.checkArgumentsNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableSet;

/**
 * The default parser. It's not thread-safe and may not be reused.
 *
 * @author Martin Kouba
 */
class DefaultParser implements Parser {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultParser.class);

    private MustacheEngine engine;

    private State state;

    private int line;

    private int delimiterIdx;

    private boolean triple;

    private StringBuilder buffer;

    private final Delimiters delimiters;

    private ParsingHandler handler;

    private final Set<String> supportedSeparators;

    private List<String> lastMatchedSeparators;

    private Set<Character> zeroIndexNonSeparatorCharacters;

    private int separatorIdx;

    /**
     *
     * @param engine
     */
    public DefaultParser(MustacheEngine engine) {
        this.state = State.TEXT;
        this.line = 1;
        this.delimiterIdx = 0;
        this.triple = false;
        this.buffer = new StringBuilder();
        this.separatorIdx = 0;
        this.engine = engine;
        this.delimiters = new Delimiters(engine.getConfiguration()
                .getStringPropertyValue(START_DELIMITER), engine
                .getConfiguration().getStringPropertyValue(END_DELIMITER));
        this.supportedSeparators = ImmutableSet.of(Strings.LINE_SEPARATOR_LF,
                Strings.LINE_SEPARATOR_CR, Strings.LINE_SEPARATOR_CRLF);
        this.zeroIndexNonSeparatorCharacters = new HashSet<Character>();
    }

    public void parse(String name, Reader reader, ParsingHandler handler) {
        checkArgumentNotEmpty(name);
        checkArgumentsNotNull(reader, handler);
        this.handler = handler;
        reader = ensureBufferedReader(reader);

        try {

            // Start of document
            handler.startTemplate(name, delimiters, engine);

            int val;
            while ((val = reader.read()) != -1) {
                processCharacter((char) val);
            }

            if (buffer.length() > 0) {
                if (state == State.TEXT) {
                    // Flush the last text segment
                    flushText();
                } else {
                    throw new MustacheException(
                            MustacheProblem.COMPILE_INVALID_TEMPLATE,
                            "Unexpected non-text buffer at the end of the document (probably unterminated tag): %s",
                            buffer);
                }
            }

            if (state == State.LINE_SEPARATOR) {
                // Flush the last line separator
                lineSeparatorFound(lastMatchedSeparators.get(0));
            }

            // End of document
            handler.endTemplate();

        } catch (IOException e) {
            throw new MustacheException(MustacheProblem.COMPILE_IO_ERROR, e);
        }
    }

    private void processCharacter(char character) {
        switch (state) {
        case TEXT:
            text(character);
            break;
        case START_TAG:
            startTag(character);
            break;
        case TAG:
            tag(character);
            break;
        case END_TAG:
            endTag(character);
            break;
        case LINE_SEPARATOR:
            lineSeparator(character);
            break;
        default:
            throw new IllegalStateException("Unknown parsing state");
        }
    }

    private void text(char character) {
        if (character == delimiters.getStart(0)) {
            if (delimiters.isStartOver(delimiterIdx)) {
                tagStartFound();
            } else {
                // Probably multi-char start tag
                state = State.START_TAG;
                delimiterIdx = 1;
            }
        } else if ((lastMatchedSeparators = findMatchingSeparators(character, 0))
                .size() > 0) {
            if (lastMatchedSeparators.size() == 1
                    && lastMatchedSeparators.get(0).length() == 1) {
                // Single-char separator
                lineSeparatorFound(lastMatchedSeparators.get(0));
            } else if ((lastMatchedSeparators.size() > 1)
                    || (lastMatchedSeparators.size() == 1 && lastMatchedSeparators
                            .get(0).length() > 1)) {
                // Multiple separators or multi-char separator
                state = State.LINE_SEPARATOR;
                separatorIdx = 1;
            }
        } else {
            buffer.append(character);
        }
    }

    private void startTag(char character) {
        if (character == delimiters.getStart(delimiterIdx)) {
            if (delimiters.isStartOver(delimiterIdx)) {
                tagStartFound();
            } else {
                delimiterIdx++;
            }
        } else {
            // False alarm - not a start delimiter
            state = State.TEXT;
            buffer.append(delimiters.getStartPart(delimiterIdx));
            delimiterIdx = 0;
            processCharacter(character);
        }
    }

    private void tag(char character) {
        if (character == delimiters.getEnd(0)) {
            if (triple) {
                // Triple mustache detected - skip first ending
                // mustache
                buffer.append(character);
                triple = false;
            } else if (delimiters.isEndOver(delimiterIdx)) {
                // One char delimiter
                flushTag();
            } else {
                // Ending tag
                state = State.END_TAG;
                delimiterIdx = 1;
            }
        } else {
            if (character == delimiters.getStart(0) && buffer.length() == 0) {
                // Most likely a triple mustache
                triple = true;
            }
            buffer.append(character);
        }
    }

    private void endTag(char character) {
        if (character == delimiters.getEnd(delimiterIdx)) {
            if (delimiters.isEndOver(delimiterIdx)) {
                flushTag();
            } else {
                delimiterIdx++;
            }
        } else {
            // False alarm - not an end delimiter
            logger.warn(
                    "Tag contains part of the end delimiter - most probably an invalid key [part: {}, line: {}]",
                    delimiters.getStartPart(delimiterIdx), line);
            state = State.TAG;
            buffer.append(delimiters.getEndPart(delimiterIdx));
            buffer.append(character);
            delimiterIdx = 0;
        }
    }

    private void lineSeparator(char character) {

        List<String> matched = findMatchingSeparators(character, separatorIdx);

        if (matched.isEmpty()) {
            // Single-char separator
            for (String separator : lastMatchedSeparators) {
                if (separator.length() == separatorIdx) {
                    lineSeparatorFound(separator);
                    processCharacter(character);
                }
            }
        } else if (matched.size() == 1) {
            // Multi-char separator
            lineSeparatorFound(matched.get(0));
        } else if (matched.size() > 1) {
            lastMatchedSeparators = matched;
            separatorIdx++;
        }
    }

    /**
     * Line separator end - flush.
     *
     * @param lineSeparator
     */
    private void lineSeparatorFound(String lineSeparator) {
        flushText();
        flushLineSeparator(lineSeparator);
        line++;
        state = State.TEXT;
        separatorIdx = 0;
    }

    /**
     * Real tag start, flush text if any.
     */
    private void tagStartFound() {
        state = State.TAG;
        delimiterIdx = 0;
        flushText();
    }

    private void flushText() {
        if (buffer.length() > 0) {
            handler.text(buffer.toString());
            clearBuffer();
        }
    }

    /**
     * Real tag end - flush.
     */
    private void flushTag() {
        state = State.TEXT;
        handler.tag(deriveTag(buffer.toString()));
        delimiterIdx = 0;
        clearBuffer();
    }

    private void flushLineSeparator(String separator) {
        handler.lineSeparator(separator);
    }

    private Reader ensureBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? reader : new BufferedReader(
                reader);
    }

    private ParsedTag deriveTag(String buffer) {
        MustacheTagType type = identifyTagType(buffer);
        String key = extractContent(type, buffer);
        return new ParsedTag(key, type);
    }

    /**
     * Identify the tag type (variable, comment, etc.).
     *
     * @param buffer
     * @param delimiters
     * @return the tag type
     */
    private MustacheTagType identifyTagType(String buffer) {

        if (buffer.length() == 0) {
            return MustacheTagType.VARIABLE;
        }

        // Triple mustache is supported for default delimiters only
        if (delimiters.hasDefaultDelimitersSet()
                && buffer.charAt(0) == ((String) EngineConfigurationKey.START_DELIMITER
                        .getDefaultValue()).charAt(0)
                && buffer.charAt(buffer.length() - 1) == ((String) EngineConfigurationKey.END_DELIMITER
                        .getDefaultValue()).charAt(0)) {
            return MustacheTagType.UNESCAPE_VARIABLE;
        }

        Character command = buffer.charAt(0);

        for (MustacheTagType type : MustacheTagType.values()) {
            if (command.equals(type.getCommand())) {
                return type;
            }
        }
        return MustacheTagType.VARIABLE;
    }

    /**
     * Extract the tag content.
     *
     * @param buffer
     * @return
     */
    private String extractContent(MustacheTagType tagType, String buffer) {

        switch (tagType) {
        case VARIABLE:
            return buffer.trim();
        case UNESCAPE_VARIABLE:
            return (buffer.charAt(0) == ((String) EngineConfigurationKey.START_DELIMITER
                    .getDefaultValue()).charAt(0) ? buffer.substring(1,
                    buffer.length() - 1).trim() : buffer.substring(1).trim());
        case SECTION:
        case INVERTED_SECTION:
        case PARTIAL:
        case EXTEND:
        case EXTEND_SECTION:
        case SECTION_END:
        case NESTED_TEMPLATE:
        case COMMENT:
            return buffer.substring(1).trim();
        case DELIMITER:
            return buffer.trim();
        default:
            return null;
        }
    }

    private void clearBuffer() {
        this.buffer = new StringBuilder();
    }

    /**
     *
     * @param character
     * @param atIndex
     * @return the list of matching line separators
     */
    private List<String> findMatchingSeparators(char character, int atIndex) {

        if (atIndex == 0 && zeroIndexNonSeparatorCharacters.contains(character)) {
            return Collections.emptyList();
        }

        List<String> matchedSeparators = new ArrayList<String>(
                supportedSeparators.size());

        for (String separator : supportedSeparators) {
            if (separator.length() > atIndex
                    && separator.charAt(atIndex) == character) {
                matchedSeparators.add(separator);
            }
        }
        if (atIndex == 0 && matchedSeparators.isEmpty()) {
            zeroIndexNonSeparatorCharacters.add(character);
        }
        return matchedSeparators;
    }

    private enum State {

        TEXT,
        START_TAG,
        TAG,
        END_TAG,
        LINE_SEPARATOR;

    }

}
