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
import static org.trimou.exception.MustacheProblem.COMPILE_INVALID_TAG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagType;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

/**
 *
 * @author Martin Kouba
 */
public class DefaultParser implements Parser {

	private static final int TEXT = 0;
	// If start delim more than one char
	private static final int START_TAG = 1;
	// Inside the tag
	private static final int TAG = 2;
	// If end delim more than one char
	private static final int END_TAG = 3;
	// Windows line sep - \r found
	private static final int START_R = 4;

	private MustacheEngine engine;

	/**
	 *
	 * @param configuration
	 * @param templateCache
	 */
	public DefaultParser(MustacheEngine engine) {
		super();
		this.engine = engine;
	}

	public void parse(String name, Reader reader, ParsingHandler handler) {

		if (name == null || reader == null || handler == null) {
			throw new NullPointerException();
		}

		reader = ensureBufferedReader(reader);

		Delimiters delimiters = new Delimiters(engine.getConfiguration()
				.getStringPropertyValue(START_DELIMITER), engine
				.getConfiguration().getStringPropertyValue(END_DELIMITER));

		StringBuilder buffer = new StringBuilder();
		int state = TEXT;
		// Delimiter index
		int delimiterIdx = 0;
		boolean triple = false;

		try {

			// Handle start of document
			handler.startTemplate(name, delimiters, engine);

			int val;
			while ((val = reader.read()) != -1) {

				char character = (char) val;

				if (state == START_TAG) {
					if (character == delimiters.getStart(delimiterIdx)) {
						if (delimiters.isStartOver(delimiterIdx)) {
							// Real tag start, flush text if any
							state = TAG;
							delimiterIdx = 0;
							buffer = flushText(buffer, handler);
						} else {
							delimiterIdx++;
						}
					} else {
						// False alarm
						state = TEXT;
						delimiterIdx = 0;
						buffer.append(delimiters.getStartPart(delimiterIdx));
						buffer.append(character);
					}
				} else if (state == TEXT) {
					if (character == delimiters.getStart(0)) {
						if (delimiters.isStartOver(delimiterIdx)) {
							// Real tag start, flush text if any
							state = TAG;
							delimiterIdx = 0;
							buffer = flushText(buffer, handler);
						} else {
							// Starting tag
							state = START_TAG;
							delimiterIdx = 1;
						}
					} else if (character == Strings.LINUX_LINE_SEPARATOR
							.charAt(0)) {
						// Line separator - flush
						state = TEXT;
						buffer = flushText(buffer, handler);
						flushLineSeparator(Strings.LINUX_LINE_SEPARATOR,
								handler);
					} else if (character == Strings.WINDOWS_LINE_SEPARATOR
							.charAt(0)) {
						state = START_R;
					} else {
						buffer.append(character);
					}
				} else if (state == TAG) {
					// Process tag contents
					if (character == delimiters.getEnd(0)) {
						if (triple) {
							// Triple mustache detected - skip first ending
							// mustache
							buffer.append(character);
							triple = false;
						} else if (delimiters.isEndOver(delimiterIdx)) {
							// Real tag end - flush
							// One char delimiter
							state = TEXT;
							delimiterIdx = 0;
							buffer = flushTag(buffer, handler, delimiters);
						} else {
							// Ending tag
							state = END_TAG;
							delimiterIdx = 1;
						}
					} else {
						if (character == delimiters.getStart(0)) {
							// Most likely a triple mustache
							triple = true;
						}
						buffer.append(character);
					}
				} else if (state == END_TAG) {
					if (character == delimiters.getEnd(delimiterIdx)) {
						if (delimiters.isEndOver(delimiterIdx)) {
							// Real tag end - flush
							state = TEXT;
							buffer = flushTag(buffer, handler, delimiters);
							delimiterIdx = 0;
						} else {
							delimiterIdx++;
						}
					} else {
						throw new MustacheException(
								MustacheProblem.COMPILE_INVALID_TAG);
					}
				} else if (state == START_R) {
					if (character == Strings.WINDOWS_LINE_SEPARATOR.charAt(1)) {
						// Line separator end - flush
						state = TEXT;
						buffer = flushText(buffer, handler);
						flushLineSeparator(Strings.WINDOWS_LINE_SEPARATOR,
								handler);
					}
				} else {
					throw new IllegalStateException("Unknown parsing state");
				}
			}

			if (buffer.length() > 0) {
				if (state == TEXT) {
					// Flush the last text segment
					flushText(buffer, handler);
				} else {
					throw new MustacheException(
							MustacheProblem.COMPILE_INVALID_TEMPLATE,
							"Remaining non-text buffer: " + buffer);
				}
			}

			// Handle end of document
			handler.endTemplate();

		} catch (IOException e) {
			throw new MustacheException(e);
		}
	}

	private StringBuilder flushText(StringBuilder buffer, ParsingHandler handler) {
		if (buffer.length() > 0) {
			handler.text(buffer.toString());
			return new StringBuilder();
		} else {
			return buffer;
		}
	}

	private StringBuilder flushTag(StringBuilder buffer, ParsingHandler handler,
			Delimiters delimiters) {
		handler.tag(deriveTag(buffer.toString(), delimiters));
		return new StringBuilder();
	}

	private void flushLineSeparator(String separator, ParsingHandler handler) {
		handler.lineSeparator(separator);
	}

	private Reader ensureBufferedReader(Reader reader) {
		return reader instanceof BufferedReader ? reader : new BufferedReader(
				reader);
	}

	private ParsedTag deriveTag(String buffer, Delimiters delimiters) {
		if (buffer.length() <= 0) {
			throw new MustacheException(COMPILE_INVALID_TAG);
		}
		MustacheTagType type = MustacheTagType.fromBuffer(buffer, delimiters);
		String key = type.extractContent(buffer);
		return new ParsedTag(key, type);
	}

}
