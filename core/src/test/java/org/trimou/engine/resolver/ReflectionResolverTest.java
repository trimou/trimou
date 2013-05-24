package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngineBuilder;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ReflectionResolverTest extends AbstractTest {

	@Test
	public void testResolution() {

		ReflectionResolver resolver = new ReflectionResolver();

		// Just to init the resolver
		MustacheEngineBuilder.newBuilder().omitServiceLoaderResolvers()
				.addResolver(resolver).build();

		Hammer hammer = new Hammer();
		assertNull(resolver.resolve(null, "whatever"));
		assertNotNull(resolver.resolve(hammer, "age"));
		assertEquals(Integer.valueOf(10), resolver.resolve(hammer, "age"));
		assertNull(resolver.resolve(hammer, "getAgeForName"));
	}

	@Test
	public void testInterpolation() {

		Map<String, Object> data = ImmutableMap.<String, Object> of("hammer",
				new Hammer());
		String templateContents = "Hello {{hammer.name}} of age {{hammer.age}}, persistent: {{hammer.persistent}} and {{hammer.invalidName}}!";

		assertEquals("Hello Edgar of age 10, persistent: false and !", engine
				.compileMustache("bean_resolver", templateContents).render(data));
	}

}
