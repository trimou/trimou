package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.Mustache;
import org.trimou.lambda.InputProcessingLambda;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class SectionSegmentTest extends AbstractTest {

	@Test
	public void testBoolean() {

		String templateContents = "{{#Boolean}}-{{/Boolean}}{{#boolean}}-{{/boolean}}!";
		Mustache mustache = engine.compileMustache("boolean", templateContents);

		assertEquals("--!", mustache.render(ImmutableMap.<String, Object> of(
				"boolean", true, "Boolean", Boolean.TRUE, "true", "true")));
		assertEquals("!", mustache.render(ImmutableMap.<String, Object> of(
				"boolean", false)));
	}

	@Test
	public void testIterable() {

		String templateContents = "{{#numbers}}la{{iterIndex}}{{iterHasNext}}|{{/numbers}}";
		Mustache mustache = engine.compileMustache("iterable", templateContents);

		assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
				"numbers", Collections.emptyList())));
		assertEquals("la1true|la2true|la3false|", mustache.render(ImmutableMap
				.<String, Object> of("numbers", ImmutableList.of(1, 2, 3))));
	}

	@Test
	public void testArray() {

		String templateContents = "{{#numbers}}la{{iterIndex}}{{iterHasNext}}|{{/numbers}}";
		Mustache mustache = engine.compileMustache("array", templateContents);

		assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
				"numbers", new Integer[] {})));
		assertEquals("la1true|la2true|la3false|", mustache.render(ImmutableMap
				.<String, Object> of("numbers", new Integer[] { 1, 2, 3 })));
	}

	@Test
	public void testLambdas() {

		Lambda literal = new SpecCompliantLambda() {

			@Override
			public String invoke(String text) {
				return "prefix_" + text;
			}

			@Override
			public boolean isReturnValueInterpolated() {
				return false;
			}
		};

		Lambda processed = new InputProcessingLambda() {

			@Override
			public String invoke(String text) {
				return "prefix_" + text;
			}

			@Override
			public boolean isReturnValueInterpolated() {
				return false;
			}
		};

		String templateContents = "{{#lambda}}{{foo}}{{/lambda}}";
		Mustache mustache = engine.compileMustache("lambda", templateContents);

		assertEquals("prefix_{{foo}}", mustache.render(ImmutableMap
				.<String, Object> of("foo", "Mine", "lambda", literal)));
		assertEquals("prefix_Mine", mustache.render(ImmutableMap
				.<String, Object> of("foo", "Mine", "lambda", processed)));
	}

	@Test
	public void testNestedContext() {

		String templateContents = "Hello {{#test}}{{name}}{{/test}}!";
		Mustache mustache = engine.compileMustache("nested", templateContents);

		assertEquals("Hello Edgar!", mustache.render(ImmutableMap
				.<String, Object> of("test", new Hammer())));
		assertEquals("Hello !",
				mustache.render(Collections.singletonMap("test", null)));
	}

}
