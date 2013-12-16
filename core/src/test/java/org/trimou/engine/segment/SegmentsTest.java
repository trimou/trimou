package org.trimou.engine.segment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class SegmentsTest {

    @Test
    public void testIsStandaloneLine() {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED,
                        false).build();

        assertTrue(Segments.isStandaloneLine(ImmutableList.<Segment> of(
                new LineSeparatorSegment(Strings.LINE_SEPARATOR_LF, null),
                new TextSegment(" ", null), new SectionSegment("test",
                        new Origin(new TemplateSegment("foo", engine))))));
        assertFalse(Segments.isStandaloneLine(ImmutableList.<Segment> of(
                new LineSeparatorSegment(Strings.LINE_SEPARATOR_LF, null),
                new TextSegment("Not empty", null), new SectionSegment("test",
                        new Origin(new TemplateSegment("foo", engine))))));
    }

}
