package org.trimou.spec;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class SpecUtilsTest {

	@Test
	public void testParsing() throws IOException {

		List<Definition> definitions = SpecUtils.parseDefinitions(SpecUtils
				.getSpecFile("comments.json", SpecTest.SPEC_VERSION));
		assertFalse(definitions.isEmpty());
	}

}
