package org.trimou.prettytime.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.ocpsoft.prettytime.i18n.Resources_en;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.LocaleSupport;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class PrettyTimeResolverTest {

	@Test
	public void testOnlyDateIsResolved() {
		PrettyTimeResolver resolver = new PrettyTimeResolver();
		assertNull(resolver.resolve(null, "prettyTime"));
		assertNull(resolver.resolve("foo", "prettyTime"));
	}

	@Test
	public void testInterpolation() {

		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.setLocaleSupport(new LocaleSupport() {

					@Override
					public Locale getCurrentLocale() {
						return Locale.ENGLISH;
					}
				}).addResolver(new PrettyTimeResolver()).build();

		assertEquals(
				new Resources_en().getString("JustNowPastPrefix"),
				engine.compileMustache("pretty", "{{now.prettyTime}}").render(
						ImmutableMap.<String, Object> of("now", new Date())));
	}

}
