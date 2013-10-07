package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class DummyTransformResolverTest extends AbstractTest {

    final DummyTransformResolver resolver = new DummyTransformResolver(100,
            new Transformer() {
                @Override
                public Object transform(Object contextObject, String name,
                        ResolutionContext context) {
                    return "{{" + name.toUpperCase() + "}}";
                }
            }, "ng");

    @Test
    public void testResolution() {

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        assertNull(resolver.resolve(null, "whatever", null));
        Object marker = resolver.resolve(null, "ng", null);
        assertNotNull(marker);
        assertEquals("{{FOO}}", resolver.resolve(marker, "foo", null));
    }

    @Test
    public void testInterpolation() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addResolver(resolver).build();
        assertEquals(
                "{{FOO}}",
                engine.compileMustache("dummy_transform_resolver", "{{ng.foo}}")
                        .render(null));

    }

}
