package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ArrayIndexResolverTest extends AbstractTest {

	@Test
	public void testInterpolation() {
		String[] stringArray = new String[] { "foo", "bar" };
		int[] intArray = new int[] { 1, 3 };
		Map<String, Object> data = ImmutableMap.<String, Object> of(
				"stringArray", stringArray, "intArray", intArray);
		String templateContents = "{{stringArray.0}},{{stringArray.1}},{{stringArray.10}},{{stringArray.a}}:{{intArray.0}},{{intArray.1}},{{intArray.10}},{{intArray.-2}}";
		assertEquals("foo,bar,,:1,3,,", engine
				.compile("list", templateContents).render(data));
	}

}
