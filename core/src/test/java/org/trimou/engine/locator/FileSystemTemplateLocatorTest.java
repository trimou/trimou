package org.trimou.engine.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class FileSystemTemplateLocatorTest extends PathTemplateLocatorTest {

	@Test
	public void testLocator() throws IOException {

		TemplateLocator locator = new FileSystemTemplateLocator(1, "src/test/resources/locator/file", "foo");

		Set<String> names = locator.getAllAvailableNames();
		assertEquals(2, names.size());
		assertTrue(names.contains("index"));
		assertTrue(names.contains("home"));

		assertEquals("{{foo}}", read(locator.locate("index")));
		assertEquals("bar", read(locator.locate("home")));
	}

	@Test
	public void testLocatorNoSuffix() throws IOException {

		TemplateLocator locator = new FileSystemTemplateLocator(1,
				"src/test/resources/locator/file");

		Set<String> names = locator.getAllAvailableNames();
		assertEquals(3, names.size());
		assertTrue(names.contains("index.foo"));
		assertTrue(names.contains("home.foo"));
		assertTrue(names.contains("detail.html"));

		assertEquals("{{foo}}", read(locator.locate("index.foo")));
		assertEquals("bar", read(locator.locate("home.foo")));
		assertEquals("<html/>", read(locator.locate("detail.html")));
	}

}
