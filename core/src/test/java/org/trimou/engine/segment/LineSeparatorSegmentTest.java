package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheEngineFactory;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.parser.Template;

/**
 *
 * @author Martin Kouba
 */
public class LineSeparatorSegmentTest extends AbstractEngineTest {

    @Override
    public void buildEngine() {
    }

    @Test
    public void testLineSeparators() {
        String templateContents = "\nHello\r\n\n!";
        Mustache mustache = MustacheEngineFactory.defaultEngine()
                .compileMustache("line_sep", templateContents);
        assertEquals("\nHello\r\n\n!", mustache.render(null));
    }

    @Test
    public void testLineSeparatorsReuse() {
        Template template = (Template) MustacheEngineBuilder
                .newBuilder()
                .setProperty(
                        EngineConfigurationKey.REUSE_LINE_SEPARATOR_SEGMENTS,
                        true).build()
                .compileMustache("line_sep_reuse_enabled", "Hello\n\n\n");
        assertEquals(4, template.getRootSegment().getSegmentsSize(false));
        assertEquals(template.getRootSegment().getSegments().get(1), template
                .getRootSegment().getSegments().get(2));
        assertEquals(template.getRootSegment().getSegments().get(1), template
                .getRootSegment().getSegments().get(3));
    }

    @Test
    public void testLineSeparatorsReuseDisabled() {
        Template template = (Template) MustacheEngineBuilder
                .newBuilder()
                .setProperty(
                        EngineConfigurationKey.REUSE_LINE_SEPARATOR_SEGMENTS,
                        false).build()
                .compileMustache("line_sep_reuse_disabled", "Hello\n\n\n");
        assertEquals(4, template.getRootSegment().getSegmentsSize(false));
        assertNotEquals(template.getRootSegment().getSegments().get(1),
                template.getRootSegment().getSegments().get(2));
        assertNotEquals(template.getRootSegment().getSegments().get(1),
                template.getRootSegment().getSegments().get(3));
    }

}
