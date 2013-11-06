package org.trimou.prettytime.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.i18n.Resources_en;
import org.ocpsoft.prettytime.units.JustNow;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
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
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setLocaleSupport(new LocaleSupport() {

                    @Override
                    public Locale getCurrentLocale() {
                        return Locale.ENGLISH;
                    }

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }
                }).addResolver(resolver).build();

        assertNull(resolver.resolve(null, "prettyTime", null));
        assertNull(resolver.resolve("foo", "prettyTime", null));
        assertNotNull(resolver.resolve(new Date(), "prettyTime", null));
        assertNotNull(resolver.resolve(10000l, "prettyTime", null));
        assertNotNull(resolver.resolve(Calendar.getInstance(), "prettyTime",
                null));
    }

    @Test
    public void testInterpolation() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setLocaleSupport(new LocaleSupport() {
                    @Override
                    public Locale getCurrentLocale() {
                        return Locale.ENGLISH;
                    }

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }
                }).addResolver(new MapResolver())
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

    @Test
    public void testCustomMatchName() {

        PrettyTimeResolver resolver = new PrettyTimeResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setLocaleSupport(new LocaleSupport() {

                    @Override
                    public Locale getCurrentLocale() {
                        return Locale.ENGLISH;
                    }

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }
                }).addResolver(resolver)
                .setProperty(PrettyTimeResolver.MATCH_NAME_KEY, "pretty")
                .build();

        assertNull(resolver.resolve(null, "pretty", null));
        assertNull(resolver.resolve("foo", "pretty", null));
        assertNotNull(resolver.resolve(new Date(), "pretty", null));
    }

    @Test
    public void testCustomPrettyTimeFactory() throws InterruptedException {

        PrettyTimeResolver resolver = new PrettyTimeResolver(100,
                new PrettyTimeFactory() {

                    @Override
                    public PrettyTime createPrettyTime(Locale locale) {

                        PrettyTime prettyTime = new PrettyTime();
                        for (TimeUnit t : prettyTime.getUnits()) {
                            if (t instanceof JustNow) {
                                ((JustNow) t).setMaxQuantity(1000L * 2L);
                            }
                        }
                        return prettyTime;
                    }
                });

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setLocaleSupport(new LocaleSupport() {

                    @Override
                    public Locale getCurrentLocale() {
                        return Locale.ENGLISH;
                    }

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }
                }).addResolver(resolver).build();

        Resources_en bundle = new Resources_en();
        assertEquals(bundle.getString("JustNowPastPrefix"), resolver.resolve(
                new Date().getTime() - 1000l, "prettyTime", null));
        assertEquals(
                "4 " + bundle.getString("SecondPluralName")
                        + bundle.getString("SecondPastSuffix"),
                resolver.resolve(new Date().getTime() - 4000l, "prettyTime",
                        null));
    }

}
