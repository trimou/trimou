package org.trimou.engine.parser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.engine.segment.ExtendSegment;
import org.trimou.engine.segment.InvertedSectionSegment;
import org.trimou.engine.segment.SectionSegment;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.SegmentType;
import org.trimou.engine.segment.TemplateSegment;

/**
 *
 * @author Martin Kouba
 */
public class ParsingTest extends AbstractEngineTest {

    @Test
    public void testVariable() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_variable", "Hello {{foo}} and {{& me}}!");

        List<Segment> segments = template.getSegments();
        assertEquals(5, segments.size());
        validateSegment(segments, 0, SegmentType.TEXT, "Hello ");
        validateSegment(segments, 1, SegmentType.VALUE, "foo");
        validateSegment(segments, 2, SegmentType.TEXT, " and ");
        validateSegment(segments, 3, SegmentType.VALUE, "me");
        validateSegment(segments, 4, SegmentType.TEXT, "!");
    }

    @Test
    public void testComment() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_comment", "{{! ignore}}{{me}}");

        List<Segment> segments = template.getSegments();
        // Comment tag is removed by default
        assertEquals(1, segments.size());
        validateSegment(segments, 0, SegmentType.VALUE, "me");
    }

    @Test
    public void testSection() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_section",
                "This is a {{#section}} jupi {{mustache}} {{/section}}");

        List<Segment> segments = template.getSegments();
        assertEquals(2, segments.size());
        validateSegment(segments, 0, SegmentType.TEXT, "This is a ");
        validateSegment(segments, 1, SegmentType.SECTION, "section");
        assertEquals(3, ((SectionSegment) segments.get(1)).getSegments().size());
        // System.out.println(template.getSegmentTreeAsString());
    }

    @Test
    public void testInvertedSection() {

        TemplateSegment template = (TemplateSegment) engine
                .compileMustache("parse_inv_section",
                        "This is a {{^section}} jupi {{/section}}");

        List<Segment> segments = template.getSegments();
        assertEquals(2, segments.size());
        validateSegment(segments, 0, SegmentType.TEXT, "This is a ");
        validateSegment(segments, 1, SegmentType.INVERTED_SECTION, "section");
        assertEquals(1, ((InvertedSectionSegment) segments.get(1))
                .getSegments().size());
    }

    @Test
    public void testDelimiters() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_delimiters",
                "This {{=%% %%=}} is a %%foo%% jupi %%={{ }}=%% {{bar}}");

        List<Segment> segments = template.getSegments();
        // Delimiters tag is removed by default
        assertEquals(6, segments.size());
        validateSegment(segments, 0, SegmentType.TEXT, "This ");
        validateSegment(segments, 1, SegmentType.TEXT, " is a ");
        validateSegment(segments, 2, SegmentType.VALUE, "foo");
        validateSegment(segments, 3, SegmentType.TEXT, " jupi ");
        validateSegment(segments, 4, SegmentType.TEXT, " ");
        validateSegment(segments, 5, SegmentType.VALUE, "bar");
    }

    @Test
    public void testPartials() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_partial", "START{{>partial}}END");

        List<Segment> segments = template.getSegments();
        assertEquals(3, segments.size());
        validateSegment(segments, 0, SegmentType.TEXT, "START");
        validateSegment(segments, 1, SegmentType.PARTIAL, "partial");
        validateSegment(segments, 2, SegmentType.TEXT, "END");
    }

    @Test
    public void testLineSeparator() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_line_sep_01", "\nHello {{foo}}\r\n\n and {{& me}}!\nAND\r\r");

        List<Segment> segments = template.getSegments();
        assertEquals(12, segments.size());
        validateSegment(segments, 0, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 1, SegmentType.TEXT, "Hello ");
        validateSegment(segments, 2, SegmentType.VALUE, "foo");
        validateSegment(segments, 3, SegmentType.LINE_SEPARATOR, "\r\n");
        validateSegment(segments, 4, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 5, SegmentType.TEXT, " and ");
        validateSegment(segments, 6, SegmentType.VALUE, "me");
        validateSegment(segments, 7, SegmentType.TEXT, "!");
        validateSegment(segments, 8, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 9, SegmentType.TEXT, "AND");
        validateSegment(segments, 10, SegmentType.LINE_SEPARATOR, "\r");
        validateSegment(segments, 11, SegmentType.LINE_SEPARATOR, "\r");


        template = (TemplateSegment) engine.compileMustache(
                "parse_line_sep_02", "\n\n ");

        segments = template.getSegments();
        assertEquals(3, segments.size());
        validateSegment(segments, 0, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 1, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 2, SegmentType.TEXT, " ");


        template = (TemplateSegment) engine.compileMustache(
                "parse_line_sep_03", "\r\n\r\n\r\r\n\n \n\r ");

        segments = template.getSegments();
        assertEquals(9, segments.size());
        validateSegment(segments, 0, SegmentType.LINE_SEPARATOR, "\r\n");
        validateSegment(segments, 1, SegmentType.LINE_SEPARATOR, "\r\n");
        validateSegment(segments, 2, SegmentType.LINE_SEPARATOR, "\r");
        validateSegment(segments, 3, SegmentType.LINE_SEPARATOR, "\r\n");
        validateSegment(segments, 4, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 5, SegmentType.TEXT, " ");
        validateSegment(segments, 6, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 7, SegmentType.LINE_SEPARATOR, "\r");
        validateSegment(segments, 8, SegmentType.TEXT, " ");
    }

    @Test
    public void testStandaloneLines() {

        TemplateSegment template = (TemplateSegment) engine
                .compileMustache("parse_standalone_line",
                        "\nHello {{foo}}\n{{! Standalone}}\n and {{& me}}!\n{{#test}}\nyes\n{{/test}}");

        List<Segment> segments = template.getSegments();
        assertEquals(9, segments.size());
        assertEquals(SegmentType.LINE_SEPARATOR, segments.get(0).getType());
        assertEquals(SegmentType.TEXT, segments.get(1).getType());
        assertEquals(SegmentType.VALUE, segments.get(2).getType());
        assertEquals(SegmentType.LINE_SEPARATOR, segments.get(3).getType());
        assertEquals(SegmentType.TEXT, segments.get(4).getType());
        assertEquals(SegmentType.VALUE, segments.get(5).getType());
        assertEquals(SegmentType.TEXT, segments.get(6).getType());
        assertEquals(SegmentType.LINE_SEPARATOR, segments.get(7).getType());
        assertEquals(SegmentType.SECTION, segments.get(8).getType());
    }

    @Test
    public void testExtendSegments() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_extend_super",
                "Hello {{$insert}}default content{{/insert}}!");

        List<Segment> segments = template.getSegments();
        assertEquals(3, segments.size());
        assertEquals(SegmentType.TEXT, segments.get(0).getType());
        assertEquals(SegmentType.EXTEND_SECTION, segments.get(1).getType());
        assertEquals(SegmentType.TEXT, segments.get(2).getType());

        segments = ((ExtendSectionSegment) segments.get(1)).getSegments();
        assertEquals(1, segments.size());
        assertEquals(SegmentType.TEXT, segments.get(0).getType());

        template = (TemplateSegment) engine
                .compileMustache(
                        "parse_extend_sub",
                        "Intro... {{<super}} skip {{$insert}}default content{{/insert}} skip! {{/super}} ...outro");

        segments = template.getSegments();
        assertEquals(3, segments.size());
        assertEquals(SegmentType.TEXT, segments.get(0).getType());
        assertEquals(SegmentType.EXTEND, segments.get(1).getType());
        assertEquals(SegmentType.TEXT, segments.get(2).getType());

        segments = ((ExtendSegment) segments.get(1)).getSegments();
        assertEquals(1, segments.size());
        assertEquals(SegmentType.EXTEND_SECTION, segments.get(0).getType());
    }

    @Test
    public void testAccentedLetters() {

        String text1 = "Teď testujeme";
        String var1 = "akcentované";
        String var2 = "ěščěšřéáíéířčžč";

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_accented_letters", text1 + "{{" + var1 + "}}{{& " + var2
                        + "}}");

        List<Segment> segments = template.getSegments();
        assertEquals(3, segments.size());
        assertEquals(SegmentType.TEXT, segments.get(0).getType());
        assertEquals(text1, segments.get(0).getText());
        assertEquals(SegmentType.VALUE, segments.get(1).getType());
        assertEquals(var1, segments.get(1).getText());
        assertEquals(SegmentType.VALUE, segments.get(2).getType());
        assertEquals(var2, segments.get(2).getText());
    }

    @Test
    public void testLineSeparatorAfterIncompleteDelimiter() {

        TemplateSegment template = (TemplateSegment) engine.compileMustache(
                "parse_line_sep_incomplete_delim",
                "{\nHello!");

        List<Segment> segments = template.getSegments();
        assertEquals(3, segments.size());
        validateSegment(segments, 0, SegmentType.TEXT, "{");
        validateSegment(segments, 1, SegmentType.LINE_SEPARATOR, "\n");
        validateSegment(segments, 2, SegmentType.TEXT, "Hello!");
    }

    private void validateSegment(List<Segment> segments, int index,
            SegmentType expectedType, String expectedText) {
        Segment segment = segments.get(index);
        assertEquals(expectedType, segment.getType());
        assertEquals(expectedText, segment.getText());
    }

}
