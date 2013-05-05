package org.trimou.engine.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.junit.Test;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.spi.engine.TemplateLocator;

/**
 *
 * @author Martin Kouba
 */
public class ClassPathTemplateLocatorTest {

	@Test
	public void testLocator() throws IOException {

		TemplateLocator locator = new ClassPathTemplateLocator(1, "foo", "locator/file");

		Set<String> names = locator.getAllAvailableNames();
		assertEquals(2, names.size());
		assertTrue(names.contains("index"));
		assertTrue(names.contains("home"));

		String index = read(locator.locate("index"));
		assertEquals("{{foo}}", index);

		String home = read(locator.locate("home"));
		assertEquals("bar", home);
	}

	private String read(Reader reader) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder text = new StringBuilder();
        int character = bufferedReader.read();
        while (character != -1) {
          text.append((char)character);
          character = bufferedReader.read();
        }
        return text.toString();
	}

}
