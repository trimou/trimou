package org.trimou.engine.config;

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
@Internal
public class ConfigurationFactory {

	/**
	 *
	 * @param builder
	 * @return the engine configuration
	 */
	public Configuration createConfiguration(MustacheEngineBuilder builder) {
		return new DefaultConfiguration(builder);
	}

}
