package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;

import com.google.common.collect.ImmutableMap;

/**
 * The very first passing test! :-)
 */
public class SimpleTest extends AbstractTest {

	@Test
	public void testInterpolation() {

		StringWriter writer = new StringWriter();
		Map<String, Object> data = ImmutableMap.<String, Object>of("foo", "me", "bar", new Hammer());
		String templateContents = "Hello {{foo}} and {{bar.name}}!";

		engine.compileMustache("foo", templateContents).render(writer, data);

		assertEquals("Hello me and Edgar!", writer.toString());
	}

}
