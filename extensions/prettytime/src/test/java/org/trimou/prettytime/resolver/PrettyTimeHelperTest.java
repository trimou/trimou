package org.trimou.prettytime.resolver;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.i18n.Resources_cs;
import org.ocpsoft.prettytime.i18n.Resources_en;
import org.ocpsoft.prettytime.units.JustNow;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.convert.Converter;
import org.trimou.engine.locale.FixedLocaleSupport;
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
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(new ThisResolver())
                .registerHelper("pretty", new PrettyTimeHelper()).build();

        String expected = new Resources_en().getString("JustNowPastPrefix");
        // JustNow the first time unit in the default list has max quantity 5
        // mins
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - 1);

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
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(new ThisResolver())
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
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(new ThisResolver())
                .registerHelper("pretty", new PrettyTimeHelper()).build();

        // JustNow the first time unit in the default list has max quantity 5
        // mins
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - 1);

        assertEquals(
                new Resources_cs().getString("JustNowPastPrefix"),
                engine.compileMustache("pretty_helper_locale",
                        "{{{pretty this locale='cs'}}}").render(now));
    }

    @Test
    public void testCustomConverter() {

        final Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, day.get(Calendar.YEAR) - 1);
        day.set(Calendar.MONTH, day.get(Calendar.MONTH) - 1);

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .addResolver(new ThisResolver())
                .registerHelper(
                        "pretty",
                        PrettyTimeHelper.builder()
                                .setConverter(new Converter<Object, Date>() {

                                    @Override
                                    public Date convert(Object from) {
                                        return day.getTime();
                                    }
                                }).build()).build();
        assertEquals("1 year ago",
                engine.compileMustache("pretty_conv", "{{pretty this}}")
                        .render(Calendar.getInstance()));
    }
}
