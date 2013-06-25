package org.trimou.tests.cdi.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.trimou.tests.IntegrationTestUtils.createCDITestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.tests.cdi.MustacheEngineProducer;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class RenderingContextTest {

	@Deployment
	public static WebArchive createTestArchive() {
		return createCDITestArchiveBase().addClasses(Foo.class, Observer.class,
				MustacheEngineProducer.class).addAsLibraries(
				getResolver().artifact("org.trimou:trimou-extension-cdi")
						.resolveAsFiles());
	}

	@Inject
	MustacheEngine engine;

	@Test
	public void testRenderingContext(Observer observer) {

		Mustache mustache = engine.compileMustache("rendering_context",
				"{{foo.createdAt}}|{{foo.createdAt}}");

		assertResult(mustache);
		assertResult(mustache);

		List<Long> timestamps = observer.getFoos();
		assertEquals(2, timestamps.size());
		assertNotEquals(timestamps.get(0), timestamps.get(1));
	}

	private void assertResult(Mustache mustache) {
		String[] parts = StringUtils.split(mustache.render(null), "|");
		assertEquals(2, parts.length);
		assertEquals(parts[0], parts[1]);
	}

}
