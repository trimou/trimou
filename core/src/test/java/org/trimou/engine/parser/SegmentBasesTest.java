package org.trimou.engine.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.engine.parser.DefaultParsingHandler.ContainerSegmentBase;
import org.trimou.engine.parser.DefaultParsingHandler.SegmentBase;
import org.trimou.engine.segment.SegmentType;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class SegmentBasesTest {

    @Test
    public void testIsStandaloneLine() {

        assertTrue(SegmentBases.isStandaloneLine(ImmutableList
                .<SegmentBase> of(new SegmentBase(SegmentType.LINE_SEPARATOR,
                        Strings.LINE_SEPARATOR_LF, 0, 0), new SegmentBase(
                        SegmentType.TEXT, " ", 0, 0), new ContainerSegmentBase(
                        SegmentType.SECTION, "test", 0, 0))));
        assertFalse(SegmentBases.isStandaloneLine(ImmutableList
                .<SegmentBase> of(new SegmentBase(SegmentType.LINE_SEPARATOR,
                        Strings.LINE_SEPARATOR_LF, 0, 0), new SegmentBase(
                        SegmentType.TEXT, "Not empty", 0, 0),
                        new ContainerSegmentBase(SegmentType.SECTION, "test",
                                0, 0))));
    }

}
