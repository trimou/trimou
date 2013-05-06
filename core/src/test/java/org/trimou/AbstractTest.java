package org.trimou;

import org.junit.Before;
import org.junit.BeforeClass;
import org.trimou.api.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public abstract class AbstractTest {

	private static final String SLF4J_DEFAULT_LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel";

	protected MustacheEngine engine;

	@BeforeClass
	public static void setLogLevel() {
		if(System.getProperty(SLF4J_DEFAULT_LOG_LEVEL_KEY) == null) {
			System.setProperty(SLF4J_DEFAULT_LOG_LEVEL_KEY, "debug");
		}
	}

	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder().build();
	}

}
