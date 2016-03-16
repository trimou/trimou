package org.trimou.tests.cdi.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.trimou.tests.IntegrationTestUtils.createCDITestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.tests.cdi.MustacheEngineProducer;
import org.trimou.util.Strings;

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
                resolve("org.trimou:trimou-extension-cdi"));
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
        List<String> parts = Strings.split(mustache.render(null), "|");
        assertEquals(2, parts.size());
        assertEquals(parts.get(0), parts.get(1));
    }

}
