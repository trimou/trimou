package org.trimou;

import org.junit.BeforeClass;

/**
 *
 * @author Martin Kouba
 */
public abstract class AbstractTest {

	private static final String SLF4J_DEFAULT_LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel";

	@BeforeClass
	public static void setLogLevel() {
		if(System.getProperty(SLF4J_DEFAULT_LOG_LEVEL_KEY) == null) {
			System.setProperty(SLF4J_DEFAULT_LOG_LEVEL_KEY, "debug");
		}
	}

}
