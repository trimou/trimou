package org.trimou.engine.segment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class SegmentsTest {

    @Test
    public void testIsStandaloneLine() {

        assertTrue(Segments.isStandaloneLine(ImmutableList.<Segment> of(
                new LineSeparatorSegment(Strings.LINE_SEPARATOR_LF, null),
                new TextSegment(" ", null), new SectionSegment("test", null))));
        assertFalse(Segments.isStandaloneLine(ImmutableList.<Segment> of(
                new LineSeparatorSegment(Strings.LINE_SEPARATOR_LF, null),
                new TextSegment("Not empty", null), new SectionSegment("test",
                        null))));
    }

}
