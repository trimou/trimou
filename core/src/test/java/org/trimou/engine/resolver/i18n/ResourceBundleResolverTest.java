package org.trimou.engine.resolver.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.LocaleSupport;

/**
 *
 * @author Martin Kouba
 */
public class ResourceBundleResolverTest extends AbstractTest {

	@Override
	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder()
				.setLocaleSupport(new LocaleSupport() {

					@Override
					public Locale getCurrentLocale() {
						return new Locale("en");
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
