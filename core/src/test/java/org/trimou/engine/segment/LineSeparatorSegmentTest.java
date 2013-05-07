package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.api.Mustache;

/**
 *
 * @author Martin Kouba
 */
public class LineSeparatorSegmentTest extends AbstractTest {

	@Test
	public void testLineSeparators() {
		String templateContents = "\nHello\r\n\n!";
		Mustache mustache = engine.compileMustache("line_sep", templateContents);
		assertEquals("\nHello\r\n\n!", mustache.render(null));
	}

}
