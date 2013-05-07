package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class BeanResolverTest extends AbstractTest {

	@Test
	public void testInterpolation() {

		Map<String, Object> data = ImmutableMap.<String, Object> of("hammer",
				new Hammer());
		String templateContents = "Hello {{hammer.name}} of age {{hammer.age}}, persistent: {{hammer.persistent}} and {{hammer.invalidName}}!";

		assertEquals("Hello Edgar of age 10, persistent: false and !", engine
				.compileMustache("bean_resolver", templateContents).render(data));
	}

}
