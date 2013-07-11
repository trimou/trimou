package org.trimou.cdi;

import org.jboss.weld.environment.se.Weld;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author Martin Kouba
 */
public abstract class WeldSETest {

	private static Weld weld;

	@BeforeClass
	public static void startWeldSE() {
		weld = new Weld();
		weld.initialize();
	}

	@AfterClass
	public static void shutdownWeldSE() {
		weld.shutdown();
	}

}
