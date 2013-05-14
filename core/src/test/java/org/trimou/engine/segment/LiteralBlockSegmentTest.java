package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class LiteralBlockSegmentTest extends AbstractTest {

	private String textParam;

	private Lambda foo = new SpecCompliantLambda() {

		@Override
		public String invoke(String text) {
			textParam = text;
			return text;
		}
	};

	@Before
	public void buildEngine() {
		textParam = null;

		MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
				"partial", "{{! No content}}", "super",
				"{{$extendMe}}Hello{{/extendMe}}"));

		engine = MustacheEngineBuilder
				.newBuilder()
				.addGlobalValue("foo", foo)
				.addTemplateLocator(locator)
				.setProperty(
						EngineConfigurationKey.REMOVE_UNNECESSARY_SEGMENTS,
						false).build();
	}

	@Test
	public void testBasicSegments() {
		// comment, value, line separator, delimiters
		String template = "{{#foo}}{{! My comment}}|{{bar}}|/n|{{=%% %%=}}|Hello%%/foo%%";
		assertEquals(
				"|true|/n||Hello",
				engine.compileMustache("literal_block_basic", template).render(
						ImmutableMap.<String, Object> of("bar", true)));
		assertEquals("{{! My comment}}|{{bar}}|/n|{{=%% %%=}}|Hello", textParam);
	}

	@Test
	public void testSection() {

		// section, inverted section
		String template = "{{#foo}} {{#section}}0{{/section}} {{^inverted}}1{{/inverted}} {{/foo}}";
		assertEquals(
				" 0 1 ",
				engine.compileMustache("literal_block_section", template).render(
						ImmutableMap.<String, Object> of("section", true,
								"inverted", false)));
		assertEquals(" {{#section}}0{{/section}} {{^inverted}}1{{/inverted}} ",
				textParam);
	}

	@Test
	public void testPartial() {
		String template = "{{#foo}}|{{>partial}}|{{/foo}}";
		assertEquals("||", engine.compileMustache("literal_block_partial", template)
				.render(null));
		assertEquals("|{{>partial}}|", textParam);
	}

	@Test
	public void testExtend() {
		String template = "{{#foo}}|{{<super}}{{/super}}|{{/foo}}";
		assertEquals("|Hello|",
				engine.compileMustache("literal_block_partial", template).render(null));
		assertEquals("|{{<super}}{{/super}}|", textParam);
	}

}
