package org.trimou.engine.listener;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
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

		final List<String> compiled = new ArrayList<String>();
		final List<String> renderingStarts = new ArrayList<String>();
		final List<String> renderingEnds = new ArrayList<String>();

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

		assertEquals(2, renderingStarts.size());
		assertEquals("listeners", renderingStarts.get(0));
		assertEquals("listeners2", renderingStarts.get(1));
		assertEquals(2, renderingEnds.size());
		assertEquals("listeners2", renderingEnds.get(0));
		assertEquals("listeners", renderingEnds.get(1));
	}
}
