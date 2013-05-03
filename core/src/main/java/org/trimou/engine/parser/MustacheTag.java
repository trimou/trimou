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

import org.trimou.engine.EngineConfigurationKey;

/**
 *
 * @author Martin Kouba
 */
public class MustacheTag {

	private String content;

	private Type type;

	public MustacheTag(String content, Type type) {
		super();
		this.content = content;
		this.type = type;
	}

	/**
	 * @return the tag content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the tag type
	 */
	public Type getType() {
		return type;
	}

	public static enum Type {

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

		Type(Character command) {
			this.command = command;
		}

		private Character command;

		public Character getCommand() {
			return command;
		}

		/**
		 * Identify the tag type (variable, comment, etc.).
		 *
		 * @param buffer
		 * @param delimiters
		 * @return the tag type
		 */
		public static Type fromBuffer(String buffer, Delimiters delimiters) {

			// Triple mustache is supported for default delimiters only
			if (delimiters.hasDefaultDelimitersSet()
					&& buffer.charAt(0) == ((String) EngineConfigurationKey.START_DELIMITER
							.getDefaultValue()).charAt(0)
					&& buffer.charAt(buffer.length() - 1) == ((String) EngineConfigurationKey.END_DELIMITER
							.getDefaultValue()).charAt(0)) {
				return UNESCAPE_VARIABLE;
			}

			Character command = buffer.charAt(0);

			for (Type type : values()) {
				if (command.equals(type.getCommand())) {
					return type;
				}
			}
			return VARIABLE;
		}

		/**
		 * Extract the tag content.
		 *
		 * @param buffer
		 * @return
		 */
		public String extractContent(String buffer) {

			switch (this) {
			case VARIABLE:
				return buffer.trim();
			case UNESCAPE_VARIABLE:
				return (buffer.charAt(0) == ((String) EngineConfigurationKey.START_DELIMITER
						.getDefaultValue()).charAt(0) ? buffer.substring(1,
						buffer.length() - 1).trim() : buffer.substring(1)
						.trim());
			case SECTION:
			case INVERTED_SECTION:
			case PARTIAL:
			case EXTEND:
			case EXTEND_SECTION:
			case SECTION_END:
			case COMMENT:
				return buffer.substring(1).trim();
			case DELIMITER:
				return buffer.trim();
			default:
				return null;
			}
		}

	}

}
