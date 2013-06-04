package org.trimou.engine.parser;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public class ParsingHandlerFactory {

	/**
	 *
	 * @return the parsing handler
	 */
	public ParsingHandler createParsingHandler() {
		return new DefaultParsingHandler();
	}

}
