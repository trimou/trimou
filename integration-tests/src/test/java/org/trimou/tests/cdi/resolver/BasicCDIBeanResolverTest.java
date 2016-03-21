package org.trimou.tests.cdi.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.trimou.tests.IntegrationTestUtils.createCDITestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.tests.cdi.MustacheEngineProducer;
import org.trimou.util.ImmutableMap;
import org.trimou.util.Strings;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicCDIBeanResolverTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createCDITestArchiveBase().addClasses(Alpha.class, Bravo.class,
                Charlie.class, Delta.class, BeanWithId.class,
                MustacheEngineProducer.class).addAsLibraries(
                resolve("org.trimou:trimou-extension-cdi"));
    }

    @Inject
    MustacheEngine engine;

    @Test
    public void testInterpolation(Alpha alpha, Bravo bravo, Charlie charlie) {

        assertNotNull(alpha);
        assertNotNull(bravo);
        assertNotNull(charlie);
        assertNotNull(engine);

        Mustache mustache = engine
                .compileMustache(
                        "cdi_bean_resolver",
                        "{{hello}}: {{#alpha}}{{id}} {{bravo.age}}{{/alpha}} {{bravo.getId}} {{charlie.id}}{{neverExisted}}");

        String result = String.format("Hello: %s 78 %s %s", alpha.getId(),
                bravo.getId(), charlie.getId());
        Map<String, Object> data = ImmutableMap.of("hello", "Hello");

        assertEquals(result, mustache.render(data));
        assertEquals(result, mustache.render(data));
    }

    @Test
    public void testDependentBeans() {

        Delta.reset();
        Mustache mustache = engine
                .compileMustache(
                        "dependent_destroyed",
                        "{{delta.createdAt}}|{{delta.createdAt}}|{{#delta}}{{createdAt}}:{{createdAt}}{{/delta}}");
        String result = mustache.render(null);

        assertNotNull(result);
        List<String> parts = Strings.split(result, "|");
        assertEquals(3, parts.size());
        assertNotEquals(Long.valueOf(parts.get(0)), Long.valueOf(parts.get(1)));
        List<String> nestedParts = Strings.split(parts.get(2), ":");
        assertNotEquals(Long.valueOf(parts.get(0)), Long.valueOf(nestedParts.get(0)));
        assertEquals(nestedParts.get(0), nestedParts.get(1));

        assertEquals(3, Delta.destructions.size());
        assertEquals(Long.valueOf(parts.get(0)), Delta.destructions.get(0));
        assertEquals(Long.valueOf(parts.get(1)), Delta.destructions.get(1));
        assertEquals(Long.valueOf(nestedParts.get(0)), Delta.destructions.get(2));
    }

}
