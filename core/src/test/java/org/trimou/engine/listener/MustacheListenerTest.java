package org.trimou.engine.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.lambda.SpecCompliantLambda;

/**
 *
 * @author Martin Kouba
 */
public class MustacheListenerTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
    }

    @Test
    public void testListenersInvoked() {

        final List<String> parsed = new ArrayList<>();
        final List<String> compiled = new ArrayList<>();
        final List<String> renderingStarts = new ArrayList<>();
        final List<String> renderingEnds = new ArrayList<>();

        SpecCompliantLambda lambda = new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return "foo";
            }
        };

        MustacheListener listener1 = new AbstractMustacheListener() {

            @Override
            public void renderingStarted(MustacheRenderingEvent event) {
                renderingStarts.add(event.getMustacheName());
            }

            @Override
            public void renderingFinished(MustacheRenderingEvent event) {
                renderingEnds.add(event.getMustacheName());
            }

            @Override
            public void compilationFinished(MustacheCompilationEvent event) {
                compiled.add(event.getMustache().getName());
            }
        };

        MustacheListener listener2 = new AbstractMustacheListener() {

            @Override
            public void renderingStarted(MustacheRenderingEvent event) {
                renderingStarts.add(event.getMustacheName() + "2");
            }

            @Override
            public void renderingFinished(MustacheRenderingEvent event) {
                renderingEnds.add(event.getMustacheName() + "2");
            }

            @Override
            public void parsingStarted(MustacheParsingEvent event) {
                parsed.add(event.getMustacheName() + "2");
            }

        };

        assertEquals(
                "foo",
                MustacheEngineBuilder.newBuilder().addGlobalData("bar", lambda)
                        .addMustacheListener(listener1)
                        .addMustacheListener(listener2).build()
                        .compileMustache("listeners", "{{bar}}").render(null));

        // Template and lambda
        assertEquals(2, compiled.size());
        assertEquals("listeners", compiled.get(0));
        // The second is one-off lambda name

        assertEquals(2, renderingStarts.size());
        assertEquals("listeners", renderingStarts.get(0));
        assertEquals("listeners2", renderingStarts.get(1));
        assertEquals(2, renderingEnds.size());
        assertEquals("listeners2", renderingEnds.get(0));
        assertEquals("listeners", renderingEnds.get(1));

        assertEquals(2, parsed.size());
        assertEquals("listeners2", parsed.get(0));
        // The second is one-off lambda name
    }

    @Test
    public void testListenerThrowsUncheckedException() {

        final List<String> renderingStarts = new ArrayList<>();
        final List<String> renderingEnds = new ArrayList<>();
        final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

        MustacheListener listener1 = new AbstractMustacheListener() {

            @Override
            public void renderingStarted(MustacheRenderingEvent event) {
                renderingStarts.add(event.getMustacheName());
            }

            @Override
            public void renderingFinished(MustacheRenderingEvent event) {
                renderingEnds.add(event.getMustacheName());
            }

        };

        MustacheListener listener2 = new AbstractMustacheListener() {

            @Override
            public void renderingStarted(MustacheRenderingEvent event) {
                renderingStarts.add(event.getMustacheName() + "2");
                event.registerReleaseCallback(new ReleaseCallback() {

                    @Override
                    public void release() {
                        callbackInvoked.set(true);
                    }
                });
            }

            @Override
            public void renderingFinished(MustacheRenderingEvent event) {
                renderingEnds.add(event.getMustacheName() + "2");
                throw new NullPointerException();
            }

        };

        try {
            MustacheEngineBuilder
                    .newBuilder()
                    .addMustacheListener(listener1)
                    .addMustacheListener(listener2)
                    .build()
                    .compileMustache("listener_throws_unchecked_exception", " ")
                    .render(null);
            fail("Rendering should fail");
        } catch (NullPointerException e) {
            // Expected
        }

        assertEquals(2, renderingStarts.size());
        assertEquals("listener_throws_unchecked_exception",
                renderingStarts.get(0));
        assertEquals("listener_throws_unchecked_exception2",
                renderingStarts.get(1));
        assertEquals(1, renderingEnds.size());
        assertEquals("listener_throws_unchecked_exception2",
                renderingEnds.get(0));
        assertTrue(callbackInvoked.get());
    }
}
