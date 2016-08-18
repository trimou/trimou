package org.trimou.engine.parser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.SegmentType;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.HelpersBuilder;

/**
 *
 * @author Martin Kouba
 */
public class NestedTemplateTest extends AbstractEngineTest {

    @Test
    public void testBasicNestedTemplate() {
        assertEquals("Hello world!",
                engine.compileMustache("nested_basic01",
                        "{{+nested}}world{{/nested}}Hello {{>nested}}!")
                        .render(null));
        assertEquals("Hello world!",
                engine.compileMustache("nested_basic02",
                        "{{+hello}}Hello{{/hello}} {{+world}}world{{/world}} {{>hello}} {{>world}}!")
                        .render(null).trim());
    }

    @Test
    public void testDependentNestedTemplates() {
        assertEquals("Hello world!",
                engine.compileMustache("nested_dependent",
                        "{{+nested1}}world{{/nested1}}{{+nested2}}{{>nested1}}{{/nested2}}Hello {{>nested2}}!")
                        .render(null));
    }

    @Test
    public void testNestedTemplateIsPreferred() {
        engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(
                        MapTemplateLocator.builder().put("foo", "bar").build())
                .build();
        assertEquals(
                "Hello world!", engine
                        .compileMustache("nested_preferred",
                                "{{+foo}}world{{/foo}}Hello {{>foo}}!")
                        .render(null));
    }

    @Test
    public void testNestedHierarchyNotSupported() {
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_NESTED_TEMPLATE_ERROR)
                .check(() -> engine.compileMustache("nested_hierarchy",
                        "{{+nested}}{{+foo}}not supported{{/foo}}{{/nested}}!"));
    }

    @Test
    public void testInvalidDefinition() {
        MustacheExceptionAssert.expect(MustacheProblem.COMPILE_INVALID_TEMPLATE)
                .check(() -> engine.compileMustache("nested_invalid_definition",
                        "{{+nested}}not supported!"));
    }

    @Test
    public void testNestedTemplateIsNotASegment() {
        Template template = (Template) engine.compileMustache(
                "nested_notsegment",
                "{{+nested}}world{{/nested}}Hello {{>nested}}!");
        List<Segment> segments = template.getRootSegment().getSegments();
        assertEquals(3, segments.size());
        ParsingTest.validateSegment(segments, 0, SegmentType.TEXT, "Hello ");
        ParsingTest.validateSegment(segments, 1, SegmentType.PARTIAL, "nested");
        ParsingTest.validateSegment(segments, 2, SegmentType.TEXT, "!");
    }

    @Test
    public void testNestedTemplateSupportDisabled() {
        engine = MustacheEngineBuilder.newBuilder()
                .addGlobalData("+foo", "Hello")
                .setProperty(
                        EngineConfigurationKey.NESTED_TEMPLATE_SUPPORT_ENABLED,
                        false)
                .build();
        assertEquals("Hello world!",
                engine.compileMustache("nested_disabled", "{{+foo}} world!")
                        .render(null));
    }

    @Test
    public void testDuplicitNamesNotAllowed() {
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_NESTED_TEMPLATE_ERROR)
                .check(() -> engine.compileMustache("nested_duplicit_names",
                        "{{+nested}}foo{{/nested}}{{+nested}}bar{{/nested}}!"));
    }

    @Test
    public void testDependentNestedTemplatesHelper() {
        engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addInclude().build())
                .build();
        assertEquals("Hello world!",
                engine.compileMustache("nested_dependent",
                        "{{+nested1}}world{{/nested1}}{{+nested2}}{{include 'nested1'}}{{/nested2}}Hello {{include 'nested2'}}!")
                        .render(null));
    }

}
