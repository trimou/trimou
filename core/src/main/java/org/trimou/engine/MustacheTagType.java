package org.trimou.engine;

import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.parser.Delimiters;

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

	/**
	 * Identify the tag type (variable, comment, etc.).
	 *
	 * @param buffer
	 * @param delimiters
	 * @return the tag type
	 */
	public static MustacheTagType fromBuffer(String buffer, Delimiters delimiters) {

		// Triple mustache is supported for default delimiters only
		if (delimiters.hasDefaultDelimitersSet()
				&& buffer.charAt(0) == ((String) EngineConfigurationKey.START_DELIMITER
						.getDefaultValue()).charAt(0)
				&& buffer.charAt(buffer.length() - 1) == ((String) EngineConfigurationKey.END_DELIMITER
						.getDefaultValue()).charAt(0)) {
			return UNESCAPE_VARIABLE;
		}

		Character command = buffer.charAt(0);

		for (MustacheTagType type : values()) {
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