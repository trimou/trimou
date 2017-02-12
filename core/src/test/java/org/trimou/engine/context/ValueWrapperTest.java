package org.trimou.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.interpolation.ThrowingExceptionMissingValueHandler;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ValueWrapperTest extends AbstractTest {

    @Test
    public void testReleaseCallbackInvoked() {

        final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addResolver(
                        new AbstractResolver(
                                Resolver.DEFAULT_PRIORITY + 100) {

                            @Override
                            public Object resolve(Object contextObject,
                                    String name, ResolutionContext context) {

                                if ("bar".equals(name)) {
                                    context.registerReleaseCallback(() -> callbackInvoked.set(true));
                                    return "foo";
                                } else {
                                    return null;
                                }
                            }
                        }).build();

        assertEquals("foo",
                engine.compileMustache("release_callback_invoked1", "{{bar}}")
                        .render(null));
        assertTrue(callbackInvoked.get());

        callbackInvoked.set(false);
        assertEquals(
                "",
                engine.compileMustache("release_callback_invoked2",
                        "{{bar.qux}}").render(null));
        assertTrue(callbackInvoked.get());
    }

    @Test
    public void testReleaseCallbackInvokedEvenIfRenderingFails() {

        final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .setMissingValueHandler(
                        new ThrowingExceptionMissingValueHandler())
                .addResolver(
                        new AbstractResolver(
                                Resolver.DEFAULT_PRIORITY + 100) {

                            @Override
                            public Object resolve(Object contextObject,
                                    String name, ResolutionContext context) {

                                context.registerReleaseCallback(() -> callbackInvoked.set(true));
                                return null;
                            }
                        }).build();

        try {
            engine.compileMustache("release_callback_invoked_rendering_fails",
                    "{{cannotmatch}}").render(null);
            fail("Rendering should fail");
        } catch (MustacheException e) {
            if (!MustacheProblem.RENDER_NO_VALUE.equals(e.getCode())) {
                fail("Unexpected problem");
            }
        }
        assertTrue(callbackInvoked.get());
    }

    @Test
    public void testReleaseCallbackFails() {

        final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addResolver(
                        new AbstractResolver(
                                Resolver.DEFAULT_PRIORITY + 100) {
                            @Override
                            public Object resolve(Object contextObject,
                                    String name, ResolutionContext context) {
                                context.registerReleaseCallback(() -> { throw new NullPointerException(); });
                                context.registerReleaseCallback(() -> callbackInvoked.set(true));
                                return null;
                            }
                        }).build();

        assertEquals(
                "",
                engine.compileMustache("release_callback_fails",
                        "{{cannotmatch}}").render(null));

        assertTrue(callbackInvoked.get());
    }

    @Test
    public void testGetKey() {

        final AtomicReference<String> key = new AtomicReference<>();

        MustacheEngineBuilder
                .newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(
                        new AbstractResolver(
                                1) {
                            @Override
                            public Object resolve(Object contextObject,
                                    String name, ResolutionContext context) {
                                key.set(context.getKey());
                                return null;
                            }
                        }).build().compileMustache("getkey", "{{my.key.foo}}")
                .render(null);

        assertEquals("my.key.foo", key.get());
    }

    @Test
    public void testGetKeyPartIndex() {

        final List<Integer> indexes = new ArrayList<>();

        MustacheEngineBuilder
                .newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(new AbstractResolver(1) {
                    @Override
                    public Object resolve(Object contextObject, String name,
                            ResolutionContext context) {
                        indexes.add(context.getKeyPartIndex());
                        return "OK";
                    }
                }).build().compileMustache("getkeypartindex", "{{my.key.foo}}")
                .render(null);

        assertEquals(3L, indexes.size());
        assertEquals(Integer.valueOf(0), indexes.get(0));
        assertEquals(Integer.valueOf(1), indexes.get(1));
        assertEquals(Integer.valueOf(2), indexes.get(2));
    }

}
