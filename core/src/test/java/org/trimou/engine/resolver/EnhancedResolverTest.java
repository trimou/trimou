package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class EnhancedResolverTest extends AbstractTest {

    @Test
    public void testHint() {

        final List<String> resolverNames = new ArrayList<String>();
        final List<String> hintNames = new ArrayList<String>();
        final AtomicInteger hintCounter = new AtomicInteger();

        EnhancedResolver resolver = new AbstractResolver(10) {

            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                resolverNames.add(name);
                return true;
            }

            @Override
            public Hint createHint(Object contextObject, String name) {
                return new Hint() {
                    @Override
                    public Object resolve(Object contextObject, String name) {
                        hintCounter.incrementAndGet();
                        if (hintCounter.get() <= 2) {
                            hintNames.add(name);
                            return false;
                        } else {
                            return null;
                        }
                    }
                };
            }

        };

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();
        Mustache mustache = engine.compileMustache("enhancedresolver_1",
                "{{foo}}");
        // Hint is not created yet
        assertEquals("true", mustache.render(null));
        // Hint applied
        assertEquals("false", mustache.render(null));
        assertEquals("false", mustache.render(null));
        // The hint returns null after two hits
        assertEquals("true", mustache.render(null));
        assertEquals(2, resolverNames.size());
        assertEquals(2, hintNames.size());
    }

}
