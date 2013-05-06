package org.trimou.tests.servlet.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.api.Mustache;
import org.trimou.api.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.servlet.RequestHolder;
import org.trimou.servlet.RequestListener;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class ServletContextTemplateLocatorTest {

	@Deployment
	public static WebArchive createTestArchive() {

		MavenDependencyResolver resolver = DependencyResolvers.use(
				MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		return ShrinkWrap
				.create(WebArchive.class)
				.addClasses(RequestHolder.class, RequestListener.class)
				.addPackage(ServletContextTemplateLocator.class.getPackage())
				// WEB-INF/templates/foo.html
				.addAsWebInfResource(new StringAsset("<html/>"),
						"templates/foo.html")
				.addAsWebInfResource(new StringAsset("<html/>"),
						"templates/qux.html")
				// templates/foo.html
				.addAsWebResource(new StringAsset("<html/>"),
						"templates/bar.html")
				.addAsLibraries(
						resolver.artifact("org.trimou:trimou-core")
								.artifact(
										"org.apache.commons:commons-lang3:3.1")
								.resolveAsFiles());
	}

	@Test
	public void testResolution() {

		ServletContextTemplateLocator locator1 = new ServletContextTemplateLocator(
				10, "html", "/WEB-INF/templates");

		ServletContextTemplateLocator locator2 = new ServletContextTemplateLocator(
				9, "html", "/templates");

		MustacheEngine factory = MustacheEngineBuilder.newBuilder()
				.addTemplateLocator(locator1).addTemplateLocator(locator2)
				.build();

		Mustache foo = factory.get("foo");
		assertNotNull(foo);
		assertEquals("<html/>", foo.render(null));

		Mustache bar = factory.get("bar");
		assertNotNull(bar);
		assertEquals("<html/>", bar.render(null));

		Set<String> locator1Names = locator1.getAllAvailableNames();
		assertEquals(2, locator1Names.size());
		assertTrue(locator1Names.contains("foo"));
		assertTrue(locator1Names.contains("qux"));
	}
}
