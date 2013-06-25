package org.trimou.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ValueReleaseCallbackTest extends AbstractTest {

	@Test
	public void testReleaseCallbackInvoked() {

		final AtomicBoolean callbackInvoked = new AtomicBoolean(false);

		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.addResolver(new AbstractResolver() {

					@Override
					public int getPriority() {
						return WithPriority.BUILTIN_RESOLVERS_DEFAULT_PRIORITY + 100;
					}

					@Override
					public Object resolve(Object contextObject, String name,
							ResolutionContext context) {

						if ("bar".equals(name)) {
							context.registerReleaseCallback(new ReleaseCallback() {

								@Override
								public void release() {
									callbackInvoked.set(true);
								}
							});
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
				.setProperty(EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM,
						true).addResolver(new AbstractResolver() {

					@Override
					public int getPriority() {
						return WithPriority.BUILTIN_RESOLVERS_DEFAULT_PRIORITY + 100;
					}

					@Override
					public Object resolve(Object contextObject, String name,
							ResolutionContext context) {

						context.registerReleaseCallback(new ReleaseCallback() {

							@Override
							public void release() {
								callbackInvoked.set(true);
							}
						});
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

		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.addResolver(new AbstractResolver() {

					@Override
					public int getPriority() {
						return WithPriority.BUILTIN_RESOLVERS_DEFAULT_PRIORITY + 100;
					}

					@Override
					public Object resolve(Object contextObject, String name,
							ResolutionContext context) {

						context.registerReleaseCallback(new ReleaseCallback() {

							@Override
							public void release() {
								throw new NullPointerException();
							}
						});
						context.registerReleaseCallback(new ReleaseCallback() {

							@Override
							public void release() {
								callbackInvoked.set(true);
							}
						});
						return null;
					}
				}).build();

		assertEquals(
				"",
				engine.compileMustache("release_callback_fails",
						"{{cannotmatch}}").render(null));

		assertTrue(callbackInvoked.get());
	}

}
