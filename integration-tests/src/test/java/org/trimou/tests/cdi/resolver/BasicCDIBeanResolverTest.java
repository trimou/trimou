package org.trimou.tests.cdi.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.trimou.tests.IntegrationTestUtils.createCDITestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import java.util.Map;

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
        Map<String, Object> data = ImmutableMap.<String, Object> of("hello",
                "Hello");

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
        String[] parts = StringUtils.split(result, "|");
        assertEquals(3, parts.length);
        assertNotEquals(Long.valueOf(parts[0]), Long.valueOf(parts[1]));
        String[] nestedParts = StringUtils.split(parts[2], ":");
        assertNotEquals(Long.valueOf(parts[0]), Long.valueOf(nestedParts[0]));
        assertEquals(nestedParts[0], nestedParts[1]);

        assertEquals(3, Delta.destructions.size());
        assertEquals(Long.valueOf(parts[0]), Delta.destructions.get(0));
        assertEquals(Long.valueOf(parts[1]), Delta.destructions.get(1));
        assertEquals(Long.valueOf(nestedParts[0]), Delta.destructions.get(2));
    }

}
