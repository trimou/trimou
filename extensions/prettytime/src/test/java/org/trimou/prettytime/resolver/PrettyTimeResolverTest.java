package org.trimou.prettytime.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.ocpsoft.prettytime.i18n.Resources_en;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.resolver.MapResolver;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class PrettyTimeResolverTest {

	@Test
	public void testFormattableObjectsResolved() {

		PrettyTimeResolver resolver = new PrettyTimeResolver();

		// Just to init the resolver
		MustacheEngineBuilder.newBuilder().omitServiceLoaderResolvers()
				.setLocaleSupport(new LocaleSupport() {

					@Override
					public Locale getCurrentLocale() {
						return Locale.ENGLISH;
					}
				}).addResolver(resolver).build();

		assertNull(resolver.resolve(null, "prettyTime"));
		assertNull(resolver.resolve("foo", "prettyTime"));
		assertNotNull(resolver.resolve(new Date(), "prettyTime"));
		assertNotNull(resolver.resolve(10000l, "prettyTime"));
		assertNotNull(resolver.resolve(Calendar.getInstance(), "prettyTime"));
	}

	@Test
	public void testInterpolation() {

		MustacheEngine engine = MustacheEngineBuilder.newBuilder()
				.omitServiceLoaderResolvers()
				.setLocaleSupport(new LocaleSupport() {

					@Override
					public Locale getCurrentLocale() {
						return Locale.ENGLISH;
					}
				})
				.addResolver(new MapResolver())
				.addResolver(new PrettyTimeResolver()).build();

		String expected = new Resources_en().getString("JustNowPastPrefix");
		Calendar now = Calendar.getInstance();

		assertEquals(expected,
				engine.compileMustache("pretty_cal", "{{now.prettyTime}}")
						.render(ImmutableMap.<String, Object> of("now", now)));
		assertEquals(
				expected,
				engine.compileMustache("pretty_date", "{{now.prettyTime}}")
						.render(ImmutableMap.<String, Object> of("now",
								now.getTime())));
		assertEquals(
				expected,
				engine.compileMustache("pretty_long", "{{now.prettyTime}}")
						.render(ImmutableMap.<String, Object> of("now",
								now.getTimeInMillis())));
	}

}
