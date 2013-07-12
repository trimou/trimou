package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.segment.SectionSegment;
import org.trimou.engine.segment.TemplateSegment;
import org.trimou.engine.segment.TextSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class SegmentImmutabilityTest extends AbstractEngineTest {

    @Test
    public void testReadonlySegments() {

        Mustache mustache = engine.compileMustache("immutable",
                "{{#section}}|{{/section}}");
        TemplateSegment template = (TemplateSegment) mustache;
        assertTrue(template.isReadOnly());
        try {
            template.iterator().remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            template.addSegment(new TextSegment("Foo", null));
            fail();
        } catch (MustacheException e) {
            if (!e.getCode().equals(
                    MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED)) {
                fail();
            }
        }
        assertEquals(1, template.getSegmentsSize());
        SectionSegment section = (SectionSegment) template.getSegments().get(0);
        assertEquals(1, section.getSegmentsSize());
        try {
            section.iterator().remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            section.addSegment(new TextSegment("Foo", null));
            fail();
        } catch (MustacheException e) {
            if (!e.getCode().equals(
                    MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED)) {
                fail();
            }
        }
    }

}
