package org.trimou.tests.servlet.resolver;

import static org.junit.Assert.assertEquals;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.Resolver;
import org.trimou.servlet.resolver.HttpServletRequestResolver;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicServletRequestResolverTest {

	@Deployment
	public static WebArchive createTestArchive() {

		return ShrinkWrap
				.create(WebArchive.class)
				.addAsLibraries(
						getResolver().artifact("org.trimou:trimou-extension-servlet")
								.resolveAsFiles())
				.addAsServiceProvider(Resolver.class,
						HttpServletRequestResolver.class);
	}

	@Test
	public void testResolution() {

		Mustache mustache = MustacheEngineBuilder
				.newBuilder()
				.build()
				.compileMustache("servlet_request_resolver_test",
						"{{request.serverPort}}");

		String result = String.format("8080");
		assertEquals(result, mustache.render(null));
	}

}
