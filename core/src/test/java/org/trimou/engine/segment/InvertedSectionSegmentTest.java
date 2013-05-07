package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.api.Mustache;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class InvertedSectionSegmentTest extends AbstractTest {

	@Test
	public void testBoolean() {
		String templateContents = "Hello {{^test}}me{{/test}}!";
		Mustache mustache = engine.compileMustache("boolean", templateContents);
		assertEquals("Hello me!", mustache.render(ImmutableMap
				.<String, Object> of("test", false)));
		assertEquals("Hello !",
				mustache.render(ImmutableMap.<String, Object> of("test", true)));
	}

	@Test
	public void testIterable() {
		String templateContents = "{{^numbers}}Hey!{{/numbers}}";
		Mustache mustache = engine.compileMustache("iterable", templateContents);
		assertEquals("Hey!", mustache.render(ImmutableMap.<String, Object> of(
				"numbers", Collections.emptyList())));
		assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
				"numbers", Collections.singleton(1))));
	}

	@Test
	public void testArray() {
		String templateContents = "{{^numbers}}Hey!{{/numbers}}";
		Mustache mustache = engine.compileMustache("iterable", templateContents);
		assertEquals("Hey!", mustache.render(ImmutableMap.<String, Object> of(
				"numbers", new Object[] {})));
		assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
				"numbers", new String[] { "Hello" })));
	}

	@Test
	public void testNestedContext() {
		String templateContents = "Hello {{^test}}ping{{/test}}!";
		Mustache mustache = engine.compileMustache("nested", templateContents);
		assertEquals("Hello !", mustache.render(ImmutableMap
				.<String, Object> of("test", new Hammer())));
		assertEquals("Hello ping!",
				mustache.render(Collections.singletonMap("test", null)));
	}

}
