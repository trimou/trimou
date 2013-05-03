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

	protected MustacheEngine engine;

	@BeforeClass
	public static void setLogLevel() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}

	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder().build();
	}

}
