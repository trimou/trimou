package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.parser.Template;

/**
 *
 * @author Martin Kouba
 */
public class SegmentImmutabilityTest extends AbstractEngineTest {

    @Test
    public void testReadonlySegments() {

        Mustache mustache = engine.compileMustache("immutable",
                "{{#section}}|{{/section}}");
        Template template = (Template) mustache;
        try {
            template.getRootSegment().iterator().remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
        assertEquals(1, template.getRootSegment().getSegmentsSize(false));
        assertEquals(2, template.getRootSegment().getSegmentsSize(true));
        SectionSegment section = (SectionSegment) template.getRootSegment().getSegments().get(0);
        assertEquals(1, section.getSegmentsSize(false));
        try {
            section.iterator().remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

}
