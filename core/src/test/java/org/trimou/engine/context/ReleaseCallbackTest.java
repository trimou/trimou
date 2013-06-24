package org.trimou.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;

/**
 *
 * @author Martin Kouba
 */
public class ReleaseCallbackTest extends AbstractTest {

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
							context.registerReleaseCallback(new ResolutionContext.ReleaseCallback() {

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
		assertEquals("",
				engine.compileMustache("release_callback_invoked2", "{{bar.qux}}")
						.render(null));
		assertTrue(callbackInvoked.get());
	}

}
