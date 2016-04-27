package org.trimou.tests.servlet.resolver;

import static org.junit.Assert.assertFalse;
import static org.trimou.tests.IntegrationTestUtils.createTestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.servlet.resolver.HttpServletRequestResolver;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicServletRequestResolverTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createTestArchiveBase().addAsLibraries(
                resolve("org.trimou:trimou-extension-servlet"));
    }

    @Test
    public void testResolution() {

        Mustache mustache = MustacheEngineBuilder
                .newBuilder()
                .setProperty(HttpServletRequestResolver.ENABLED_KEY, true)
                .build()
                .compileMustache("servlet_request_resolver_test",
                        "{{request.method}}");

        assertFalse(mustache.render(null).isEmpty());
    }

}
