package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.Options;

/**
 *
 * @author Martin Kouba
 */
public class EnhancedResolverTest extends AbstractTest {

    @Test
    public void testHint() {

        final List<String> resolvedNames = new ArrayList<>();
        final List<String> hintNames = new ArrayList<>();
        final AtomicInteger hintCounter = new AtomicInteger();

        EnhancedResolver resolver = new AbstractResolver(10) {

            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                resolvedNames.add(name);
                return true;
            }

            @Override
            public Hint createHint(Object contextObject, String name, ResolutionContext context) {
                return (ctxObj, n, ctx) -> {
                    hintCounter.incrementAndGet();
                    if (hintCounter.get() <= 2) {
                        hintNames.add(n);
                        return false;
                    } else {
                        return null;
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
        assertEquals(2, resolvedNames.size());
        assertEquals(2, hintNames.size());
    }

    @Test
    public void testHintIsOnlyUsedForTheFirstPartOfKey() {

        final List<String> resolvedNames = new ArrayList<>();
        final List<String> hintNames = new ArrayList<>();
        final AtomicInteger hintCounter = new AtomicInteger();
        final Hammer hammer1 = new Hammer(10);
        final Hammer hammer2 = new Hammer(20);

        EnhancedResolver resolver = new AbstractResolver(10) {

            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                resolvedNames.add(name);
                if (contextObject == null) {
                    return hammer1;
                } else if (contextObject instanceof Hammer) {
                    return ((Hammer) contextObject).getAge();
                }
                return null;
            }

            @Override
            public Hint createHint(Object contextObject, String name, ResolutionContext context) {
                return (ctxObj, n, ctx) -> {
                    hintCounter.incrementAndGet();
                    if (hintCounter.get() <= 2) {
                        hintNames.add(n);
                        return hammer2;
                    } else {
                        return null;
                    }
                };
            }

        };
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();
        Mustache mustache = engine.compileMustache("enhancedresolver_2",
                "{{foo.bar}}");
        // Hint is not created yet
        assertEquals("10", mustache.render(null));
        // Hint applied
        assertEquals("20", mustache.render(null));
        assertEquals("20", mustache.render(null));
        // The hint returns null after two hits
        assertEquals("10", mustache.render(null));
        assertEquals(6, resolvedNames.size());
        assertEquals(2, hintNames.size());
    }

    @Test
    public void testHintIsNotCreatedForHelper() {

        final AtomicBoolean hintCreate = new AtomicBoolean(false);
        EnhancedResolver resolver = new AbstractResolver(10) {

            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                return true;
            }

            @Override
            public Hint createHint(Object contextObject, String name, ResolutionContext context) {
                hintCreate.set(true);
                return null;
            }

        };
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).registerHelper("foo", new BasicHelper() {
                    @Override
                    public void execute(Options options) {
                        append(options, options.getValue("foo").toString());
                    }
                }).build();
        Mustache mustache = engine.compileMustache("enhancedresolver_helper1",
                "{{foo 'bar'}}");
        assertEquals("true", mustache.render(null));
        assertFalse(hintCreate.get());
    }

}
