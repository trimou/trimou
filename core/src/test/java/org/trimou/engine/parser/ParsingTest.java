package org.trimou.engine.parser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
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
public class ParsingTest extends AbstractTest {

	@Test
	public void testVariable() {

		TemplateSegment template = (TemplateSegment) engine.compileMustache(
				"parse_variable", "Hello {{foo}} and {{& me}}!");

		List<Segment> segments = template.getSegments();
		assertEquals(5, segments.size());
		assertEquals(SegmentType.TEXT, segments.get(0).getType());
		assertEquals(SegmentType.VALUE, segments.get(1).getType());
		assertEquals(SegmentType.TEXT, segments.get(2).getType());
		assertEquals(SegmentType.VALUE, segments.get(1).getType());
		assertEquals(SegmentType.TEXT, segments.get(2).getType());
	}

	@Test
	public void testComment() {

		TemplateSegment template = (TemplateSegment) engine.compileMustache(
				"parse_comment", "{{! ignore}}{{me}}");

		List<Segment> segments = template.getSegments();
		assertEquals(1, segments.size());
		assertEquals(SegmentType.VALUE, segments.get(0).getType());
	}

	@Test
	public void testSection() {

		TemplateSegment template = (TemplateSegment) engine.compileMustache(
				"parse_section",
				"This is a {{#section}} jupi {{mustache}} {{/section}}");

		List<Segment> segments = template.getSegments();
		assertEquals(2, segments.size());
		assertEquals(SegmentType.TEXT, segments.get(0).getType());
		assertEquals(SegmentType.SECTION, segments.get(1).getType());
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
		assertEquals(SegmentType.TEXT, segments.get(0).getType());
		assertEquals(SegmentType.INVERTED_SECTION, segments.get(1).getType());
		assertEquals(1, ((InvertedSectionSegment) segments.get(1))
				.getSegments().size());
	}

	@Test
	public void testDelimiters() {

		TemplateSegment template = (TemplateSegment) engine.compileMustache(
				"parse_delimiters",
				"This {{=%% %%=}} is a %%foo%% jupi %%={{ }}=%% {{bar}}");

		List<Segment> segments = template.getSegments();
		assertEquals(6, segments.size());
		assertEquals(SegmentType.TEXT, segments.get(0).getType());
		assertEquals(SegmentType.TEXT, segments.get(1).getType());
		assertEquals(SegmentType.VALUE, segments.get(2).getType());
		assertEquals(SegmentType.TEXT, segments.get(3).getType());
		assertEquals(SegmentType.TEXT, segments.get(4).getType());
		assertEquals(SegmentType.VALUE, segments.get(5).getType());
	}

	@Test
	public void testPartials() {

		TemplateSegment template = (TemplateSegment) engine.compileMustache(
				"parse_partial", "START{{>partial}}END");

		List<Segment> segments = template.getSegments();
		assertEquals(3, segments.size());
		assertEquals(SegmentType.TEXT, segments.get(0).getType());
		assertEquals(SegmentType.PARTIAL, segments.get(1).getType());
		assertEquals(SegmentType.TEXT, segments.get(2).getType());
	}

	@Test
	public void testLineSeparator() {

		TemplateSegment template = (TemplateSegment) engine.compileMustache(
				"parse_line_sep", "\nHello {{foo}}\r\n\n and {{& me}}!\n");

		List<Segment> segments = template.getSegments();
		assertEquals(9, segments.size());
		assertEquals(SegmentType.LINE_SEPARATOR, segments.get(0).getType());
		assertEquals(SegmentType.TEXT, segments.get(1).getType());
		assertEquals(SegmentType.VALUE, segments.get(2).getType());
		assertEquals(SegmentType.LINE_SEPARATOR, segments.get(3).getType());
		assertEquals(SegmentType.LINE_SEPARATOR, segments.get(4).getType());
		assertEquals(SegmentType.TEXT, segments.get(5).getType());
		assertEquals(SegmentType.VALUE, segments.get(6).getType());
		assertEquals(SegmentType.TEXT, segments.get(7).getType());
		assertEquals(SegmentType.LINE_SEPARATOR, segments.get(8).getType());
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

}
