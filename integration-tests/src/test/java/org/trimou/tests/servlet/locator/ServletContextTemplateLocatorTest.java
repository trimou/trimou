package org.trimou.tests.servlet.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.api.Mustache;
import org.trimou.api.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class ServletContextTemplateLocatorTest {

	@Deployment
	public static WebArchive createTestArchive() {

		return ShrinkWrap
				.create(WebArchive.class)
				// WEB-INF/templates/foo.html
				.addAsWebInfResource(new StringAsset("<html/>"),
						"templates/foo.html")
				.addAsWebInfResource(new StringAsset("<html/>"),
						"templates/qux.html")
				// templates/foo.html
				.addAsWebResource(new StringAsset("<html/>"),
						"templates/bar.html")
				.addAsLibraries(
						getResolver().artifact("org.trimou:trimou-extension-servlet")
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

		Mustache foo = factory.getMustache("foo");
		assertNotNull(foo);
		assertEquals("<html/>", foo.render(null));

		Mustache bar = factory.getMustache("bar");
		assertNotNull(bar);
		assertEquals("<html/>", bar.render(null));

		Set<String> locator1Names = locator1.getAllAvailableNames();
		assertEquals(2, locator1Names.size());
		assertTrue(locator1Names.contains("foo"));
		assertTrue(locator1Names.contains("qux"));
	}
}
