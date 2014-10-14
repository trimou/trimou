package org.trimou.prettytime.resolver;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.i18n.Resources_cs;
import org.ocpsoft.prettytime.i18n.Resources_en;
import org.ocpsoft.prettytime.units.JustNow;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.resolver.ThisResolver;
import org.trimou.prettytime.PrettyTimeFactory;
import org.trimou.prettytime.PrettyTimeHelper;

/**
 *
 * @author Martin Kouba
 */
public class PrettyTimeHelperTest {

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
                }).addResolver(new ThisResolver())
                .registerHelper("pretty", new PrettyTimeHelper()).build();

        String expected = new Resources_en().getString("JustNowPastPrefix");
        Calendar now = Calendar.getInstance();

        assertEquals(
                expected,
                engine.compileMustache("pretty_cal", "{{pretty this}}").render(
                        now));
        assertEquals(expected,
                engine.compileMustache("pretty_date", "{{pretty this}}")
                        .render(now.getTime()));
        assertEquals(expected,
                engine.compileMustache("pretty_long", "{{pretty this}}")
                        .render(now.getTimeInMillis()));
    }

    @Test
    public void testCustomPrettyTimeFactory() throws InterruptedException {

        PrettyTimeHelper helper = new PrettyTimeHelper(new PrettyTimeFactory() {

            @Override
            public PrettyTime createPrettyTime(Locale locale) {
                PrettyTime prettyTime = new PrettyTime(locale);
                prettyTime.getUnit(JustNow.class).setMaxQuantity(1000L * 2L);
                return prettyTime;
            }
        });

        // Just to init the resolver
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
                }).addResolver(new ThisResolver())
                .registerHelper("pretty", helper).build();

        Resources_en bundle = new Resources_en();
        assertEquals(
                bundle.getString("JustNowPastPrefix"),
                engine.compileMustache("pretty_helper_custom_factory_01",
                        "{{pretty this}}").render(new Date().getTime() - 1000l));
        assertEquals(
                "4 " + bundle.getString("SecondPluralName")
                        + bundle.getString("SecondPastSuffix"),
                engine.compileMustache("pretty_helper_custom_factory_02",
                        "{{pretty this}}").render(new Date().getTime() - 4000l));
    }

    @Test
    public void testLocale() {

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
                }).addResolver(new ThisResolver())
                .registerHelper("pretty", new PrettyTimeHelper()).build();

        assertEquals( new Resources_cs().getString("JustNowPastPrefix"), engine.compileMustache("pretty_helper_locale", "{{{pretty this locale='cs'}}}").render(new Date()));
    }
}
