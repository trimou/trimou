package org.trimou.engine.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.parser.DefaultHandler;
import org.trimou.engine.segment.LineSeparatorSegment;
import org.trimou.engine.segment.SectionSegment;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.TextSegment;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class DefaultHandlerTest extends AbstractTest {

	@Test
	public void testIsStandaloneLine() {

		DefaultHandler handler = new DefaultHandler();
		assertTrue(handler.isStandaloneLine(ImmutableList.<Segment> of(
				new LineSeparatorSegment(Strings.LINUX_LINE_SEPARATOR, null),
				new TextSegment(" ", null), new SectionSegment("test", null))));
		assertFalse(handler.isStandaloneLine(ImmutableList.<Segment> of(
				new LineSeparatorSegment(Strings.LINUX_LINE_SEPARATOR, null),
				new TextSegment("Not empty", null), new SectionSegment("test",
						null))));
	}

}
