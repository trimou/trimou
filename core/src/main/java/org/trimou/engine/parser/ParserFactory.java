package org.trimou.engine.parser;

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;

/**
 *
 * @author Martin Kouba
 */
@Internal
public class ParserFactory {

	/**
	 *
	 * @param engine
	 * @return the parser
	 */
	public Parser createParser(MustacheEngine engine) {
		return new DefaultParser(engine);
	}

}
