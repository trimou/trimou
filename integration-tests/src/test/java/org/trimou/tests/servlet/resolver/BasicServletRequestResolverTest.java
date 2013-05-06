package org.trimou.tests.servlet.resolver;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.api.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.servlet.resolver.HttpServletRequestResolver;
import org.trimou.spi.engine.Resolver;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicServletRequestResolverTest {

	@Deployment
	public static WebArchive createTestArchive() {

		MavenDependencyResolver resolver = DependencyResolvers.use(
				MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		return ShrinkWrap
				.create(WebArchive.class)
				.addPackage(HttpServletRequestResolver.class.getPackage())
				.addAsLibraries(
						resolver.artifact("org.trimou:trimou-core")
								.resolveAsFiles())
				.addAsServiceProvider(Resolver.class,
						HttpServletRequestResolver.class);
	}

	@Test
	public void testResolution() {

		Mustache mustache = MustacheEngineBuilder
				.newBuilder()
				.build()
				.compile("servlet_request_resolver_test",
						"{{request.serverPort}}");

		String result = String.format("8080");

		StringWriter writer = new StringWriter();
		mustache.render(writer, null);
		assertEquals(result, writer.toString());
	}

}
