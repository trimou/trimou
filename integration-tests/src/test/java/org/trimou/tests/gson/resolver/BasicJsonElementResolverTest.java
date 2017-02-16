package org.trimou.tests.gson.resolver;

import static org.junit.Assert.assertEquals;
import static org.trimou.tests.IntegrationTestUtils.createTestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import java.io.StringReader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineFactory;

import com.google.gson.JsonParser;

/**
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicJsonElementResolverTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createTestArchiveBase().addAsLibraries(
                resolve("org.trimou:trimou-extension-gson"));
    }

    @Test
    public void testInterpolation() {
        Mustache mustache = MustacheEngineFactory.defaultEngine()
                .compileMustache("json_element_resolver_test", "{{foo.name}}");
        assertEquals("Jachym",
                mustache.render(new JsonParser().parse(new StringReader(
                        "{ \"foo\": { \"name\": \"Jachym\"}}"))));
    }
}
