package org.trimou.spec;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class SpecTest {

	static final String SPEC_VERSION = "1_1_2";

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
	}

	@Test
	public void testComments() throws IOException {
		SpecUtils.executeTests("comments.json", SPEC_VERSION);
	}

	@Test
	public void testSections() throws IOException {
		SpecUtils.executeTests("sections.json", SPEC_VERSION);
	}

	@Test
	public void testInvertedSections() throws IOException {
		SpecUtils.executeTests("inverted.json", SPEC_VERSION);
	}

	@Test
	public void testDelimiters() throws IOException {
		SpecUtils.executeTests("delimiters.json", SPEC_VERSION);
	}

	@Test
	public void testInterpolation() throws IOException {
		SpecUtils.executeTests("interpolation.json", SPEC_VERSION);
	}

	@Test
	public void testPartials() throws IOException {
		SpecUtils.executeTests("partials.json", SPEC_VERSION);
	}

	@Test
	public void testLambdas() throws IOException {
		// "Section - Alternate Delimiters" which would fail is not defined in
		// JSON file
		SpecUtils.executeTests("~lambdas.json", SPEC_VERSION);
	}

}
