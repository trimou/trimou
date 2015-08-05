package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.engine.MustacheTagType;

/**
 *
 * @author Martin Kouba
 */
public class SegmentTypeTest {

    @Test
    public void testFromTag() {
        for (SegmentType segmentType : SegmentType.values()) {
            if(segmentType.getTagType() != null) {
                assertEquals(segmentType, SegmentType.fromTag(segmentType.getTagType()));
            }
        }
        assertEquals(SegmentType.VALUE, SegmentType.fromTag(MustacheTagType.UNESCAPE_VARIABLE));
    }

}
