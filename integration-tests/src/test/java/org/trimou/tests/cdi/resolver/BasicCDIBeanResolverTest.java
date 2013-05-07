package org.trimou.tests.cdi.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.trimou.tests.IntegrationTestUtils.createCDITestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.api.Mustache;
import org.trimou.api.engine.MustacheEngine;
import org.trimou.tests.cdi.MustacheEngineProducer;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicCDIBeanResolverTest {

	@Deployment
	public static WebArchive createTestArchive() {
		return createCDITestArchiveBase().addClasses(Alpha.class, Bravo.class,
				Charlie.class, BeanWithId.class, MustacheEngineProducer.class)
				.addAsLibraries(
						getResolver().artifact(
								"org.trimou:trimou-extension-cdi")
								.resolveAsFiles());
	}

	@Inject
	Alpha alpha;

	@Inject
	Bravo bravo;

	@Inject
	Charlie charlie;

	@Inject
	MustacheEngine engine;

	@Test
	public void testInterpolation() {

		assertNotNull(alpha);
		assertNotNull(bravo);
		assertNotNull(charlie);
		assertNotNull(engine);

		Mustache mustache = engine
				.compileMustache(
						"cdi_bean_resolver_test",
						"{{hello}}: {{#alpha}}{{id}} {{bravo.age}}{{/alpha}} {{bravo.id}} {{charlie.id}}{{neverExisted}}");

		String result = String.format("Hello: %s 78 %s %s", alpha.getId(),
				bravo.getId(), charlie.getId());
		Map<String, Object> data = ImmutableMap.<String, Object> of("hello",
				"Hello");

		assertEquals(result, mustache.render(data));
		assertEquals(result, mustache.render(data));
	}

}
