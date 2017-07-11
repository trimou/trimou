package org.trimou.prettytime.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.i18n.Resources_en;
import org.ocpsoft.prettytime.units.JustNow;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.FixedLocaleSupport;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class PrettyTimeResolverTest {

    @Test
    public void testFormattableObjectsResolved() {

        PrettyTimeResolver resolver = new PrettyTimeResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setProperty(PrettyTimeResolver.ENABLED_KEY, true)
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(resolver).build();

        assertNull(resolver.resolve(null, "prettyTime", null));
        assertNull(resolver.resolve("foo", "prettyTime", null));
        assertNotNull(resolver.resolve(new Date(), "prettyTime", null));
        assertNotNull(resolver.resolve(10000L, "prettyTime", null));
        assertNotNull(
                resolver.resolve(Calendar.getInstance(), "prettyTime", null));
    }

    @Test
    public void testInterpolation() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setProperty(PrettyTimeResolver.ENABLED_KEY, true)
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(new MapResolver())
                .addResolver(new PrettyTimeResolver()).build();

        String expected = new Resources_en().getString("JustNowPastPrefix");
        // JustNow (the first time unit in the default list) has max quantity 1 min
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, -30);

        assertEquals(expected,
                engine.compileMustache("pretty_cal", "{{now.prettyTime}}")
                        .render(ImmutableMap.<String, Object> of("now", now)));
        assertEquals(expected,
                engine.compileMustache("pretty_date", "{{now.prettyTime}}")
                        .render(ImmutableMap.<String, Object> of("now",
                                now.getTime())));
        assertEquals(expected,
                engine.compileMustache("pretty_long", "{{now.prettyTime}}")
                        .render(ImmutableMap.<String, Object> of("now",
                                now.getTimeInMillis())));
    }

    @Test
    public void testCustomMatchName() {

        PrettyTimeResolver resolver = new PrettyTimeResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setProperty(PrettyTimeResolver.ENABLED_KEY, true)
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(resolver)
                .setProperty(PrettyTimeResolver.MATCH_NAME_KEY, "pretty")
                .build();

        assertNull(resolver.resolve(null, "pretty", null));
        assertNull(resolver.resolve("foo", "pretty", null));
        assertNotNull(resolver.resolve(new Date(), "pretty", null));
    }

    @Test
    public void testCustomPrettyTimeFactory() throws InterruptedException {

        PrettyTimeResolver resolver = new PrettyTimeResolver(100,
                locale -> {
                    PrettyTime prettyTime = new PrettyTime(locale);
                    TimeFormat timeFormat = prettyTime.removeUnit(JustNow.class);
                    JustNow justNow = new JustNow();
                    justNow.setMaxQuantity(1000L * 2L);
                    prettyTime.registerUnit(justNow, timeFormat);
                    return prettyTime;
                });

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setProperty(PrettyTimeResolver.ENABLED_KEY, true)
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(resolver).build();

        Resources_en bundle = new Resources_en();
        assertEquals(bundle.getString("JustNowPastPrefix"), resolver
                .resolve(new Date().getTime() - 1000L, "prettyTime", null));
        assertEquals(
                "4 " + bundle.getString("SecondPluralName")
                        + bundle.getString("SecondPastSuffix"),
                resolver.resolve(new Date().getTime() - 4000L, "prettyTime",
                        null));
    }

    @Test
    public void testDisabledResolver() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(new PrettyTimeResolver()).build();

        assertTrue(engine.getConfiguration().getResolvers().isEmpty());
        assertEquals("", engine.compileMustache("disabled_prettytime_resolver",
                "{{this.prettyTime}}").render(new Date()));
    }

}
