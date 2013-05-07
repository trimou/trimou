package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.api.Mustache;
import org.trimou.api.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ExtendSegmentTest extends AbstractTest {

	@Before
	public void buildEngine() {
	}

	@Test
	public void testSimpleInheritance() {

		MapTemplateLocator locator = new MapTemplateLocator(
				ImmutableMap
						.of("super", "Hello {{$insert}}Martin{{/insert}}",
								"sub",
								"And now... {{<super}} {{$insert}}{{name}}{{/insert}} {{/super}}!"));
		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.addTemplateLocator(locator).build();
		Mustache sub = engine.getMustache("sub");

		assertEquals("And now... Hello Edgar!",
				sub.render(ImmutableMap.<String, Object> of("name", "Edgar")));
	}

	@Test
	public void testMultipleInheritance() {

		MapTemplateLocator locator = new MapTemplateLocator(
				ImmutableMap
						.of("super",
								"for {{$insert}}{{/insert}}",
								"sub",
								"And now {{<super}} {{$insert}}something {{$insert2}}{{/insert2}} different{{/insert}} {{/super}}.",
								"subsub",
								"{{<sub}} {{$insert2}}completely{{/insert2}} {{/sub}}"));
		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.addTemplateLocator(locator).build();
		Mustache sub = engine.getMustache("subsub");

		StringWriter writer = new StringWriter();
		sub.render(writer, null);
		assertEquals("And now for something completely different.",
				writer.toString());
	}

	@Test
	public void testMultipleInheritanceOverride() {

		MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
				"super", "{{$insert}}{{/insert}}", "sub",
				"{{<super}} {{$insert}}false{{/insert}} {{/super}}", "subsub",
				"{{<sub}} {{$insert}}true{{/insert}} {{/sub}}"));
		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.addTemplateLocator(locator).build();
		Mustache sub = engine.getMustache("subsub");

		StringWriter writer = new StringWriter();
		sub.render(writer, null);
		assertEquals("true", writer.toString());
	}

}
