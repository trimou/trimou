package org.trimou.engine.resolver.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;

/**
 *
 * @author Martin Kouba
 */
public class ResourceBundleResolverTest extends AbstractEngineTest {

	@Override
	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder()
				.setLocaleSupport(new LocaleSupport() {
					@Override
					public Locale getCurrentLocale() {
						return new Locale("en");
					}
					@Override
					public void init(Configuration configuration) {
					}
					@Override
					public Set<ConfigurationKey> getConfigurationKeys() {
						return Collections.emptySet();
					}
				}).addResolver(new ResourceBundleResolver("messages")).build();

	}

	@Test
	public void testInterpolation() {

		String templateContents = "{{messages.echo_one}}";
		Mustache mustache = engine.compileMustache("bundle", templateContents);

		assertEquals("Hello", mustache.render(null));
	}

}
