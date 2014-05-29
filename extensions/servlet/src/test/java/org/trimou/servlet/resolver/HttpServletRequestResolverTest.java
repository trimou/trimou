package org.trimou.servlet.resolver;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class HttpServletRequestResolverTest {

    @Test
    public void testNulltIsResolved() {
        HttpServletRequestResolver resolver = new HttpServletRequestResolver();
        assertNull(resolver.resolve("whatever", "request", null));
        assertNull(resolver.resolve(null, "foo", null));
    }

    @Test
    public void testDisabledResolver() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(new HttpServletRequestResolver())
                .setProperty(HttpServletRequestResolver.ENABLED_KEY, false).build();
        assertTrue(engine.getConfiguration().getResolvers().isEmpty());
    }

}
