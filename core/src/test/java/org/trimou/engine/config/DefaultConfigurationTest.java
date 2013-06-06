package org.trimou.engine.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ReflectionResolver;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Martin Kouba
 */
public class DefaultConfigurationTest extends AbstractEngineTest {

	private final ConfigurationKey testResolverKeyAlpha = new SimpleConfigurationKey(
			"test.key.alpha", true);

	private final ConfigurationKey testResolverKeyBravo = new SimpleConfigurationKey(
			"test.key.bravo", 1l);

	@Before
	public void buildEngine() {

		System.setProperty(
				EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK.get(),
				"true");
		System.setProperty(
				ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY.get(),
				"2000");
		System.setProperty("test.key.bravo", "1000");

		engine = MustacheEngineBuilder
				.newBuilder()
				.setProperty(
						ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY,
						"3000").addResolver(new AbstractResolver() {

					@Override
					public int getPriority() {
						return 0;
					}

					@Override
					public Object resolve(Object contextObject, String name) {
						return null;
					}

					@Override
					public Set<ConfigurationKey> getConfigurationKeys() {
						return ImmutableSet.<ConfigurationKey> of(
								testResolverKeyAlpha, testResolverKeyBravo);
					}

				}).build();

	}

	@After
	public void resetSystemProperties() {
		System.setProperty(EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK
				.get(), EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK
				.getDefaultValue().toString());
		System.setProperty(ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY
				.get(), ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY
				.getDefaultValue().toString());
	}

	@Test
	public void testInitializeProperties() {

		// Default values
		assertEquals(
				EngineConfigurationKey.START_DELIMITER.getDefaultValue(),
				engine.getConfiguration().getStringPropertyValue(
						EngineConfigurationKey.START_DELIMITER));
		assertEquals(
				EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES
						.getDefaultValue(),
				engine.getConfiguration().getBooleanPropertyValue(
						EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES));

		// System prop
		assertTrue(engine.getConfiguration().getBooleanPropertyValue(
				EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK));

		// File prop
		assertFalse(engine.getConfiguration().getBooleanPropertyValue(
				testResolverKeyAlpha));

		// Manually set prop vs system prop priority
		assertEquals(
				Long.valueOf(3000),
				engine.getConfiguration().getLongPropertyValue(
						ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY));

		// System prop vs file prop priority
		assertEquals(Long.valueOf(1000), engine.getConfiguration()
				.getLongPropertyValue(testResolverKeyBravo));
	}
}
