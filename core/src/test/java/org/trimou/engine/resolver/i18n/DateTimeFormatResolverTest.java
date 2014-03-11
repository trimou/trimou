package org.trimou.engine.resolver.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;

import com.google.common.collect.ImmutableMap;

/**
 * @author Martin Kouba
 */
public class DateTimeFormatResolverTest extends AbstractEngineTest {

    private DateTimeFormatResolver resolver;

    @Override
    @Before
    public void buildEngine() {
        resolver = new DateTimeFormatResolver();
        engine = MustacheEngineBuilder
                .newBuilder()
                .setProperty(DateTimeFormatResolver.CUSTOM_PATTERN_KEY,
                        "DD-MM-yyyy HH:mm")
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
    }

    @Test
    public void testResolution() {
        assertNull(resolver.resolve(new Date(), "foo", null));
        assertNull(resolver.resolve("5", "foo", null));
        assertNotNull(resolver.resolve(5, DateTimeFormatResolver.NAME_FORMAT,
                null));
        assertNotNull(resolver.resolve(Calendar.getInstance(),
                DateTimeFormatResolver.NAME_FORMAT_CUSTOM, null));
        assertNotNull(resolver.resolve(new Date(),
                DateTimeFormatResolver.NAME_FORMAT_DATE, null));
    }

    @Test
    public void testInterpolation() {

        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2013);
        day.set(Calendar.MONTH, 0);
        day.set(Calendar.DAY_OF_MONTH, 1);
        day.set(Calendar.HOUR_OF_DAY, 13);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);

        long milis = day.getTimeInMillis();

        String expectedShort = "1/1/13 1:00 PM";
        String expectedMedium = "Jan 1, 2013 1:00:00 PM";
        String expectedCustom = "01-01-2013 13:00";
        String expectedDate = "Jan 1, 2013";

        Map<String, Object> data = ImmutableMap.<String, Object> of("calendar",
                day, "date", day.getTime(), "milis", milis);

        assertEquals(
                StringUtils.repeat(expectedMedium, "|", 3),
                engine.compileMustache("date_time_medium",
                        "{{calendar.format}}|{{date.format}}|{{milis.format}}")
                        .render(data));
        assertEquals(
                StringUtils.repeat(expectedShort, "|", 3),
                engine.compileMustache("date_time_short",
                        "{{calendar.formatShort}}|{{date.formatShort}}|{{milis.formatShort}}")
                        .render(data));
        assertEquals(
                StringUtils.repeat(expectedCustom, "|", 3),
                engine.compileMustache("date_time_custom",
                        "{{calendar.formatCustom}}|{{date.formatCustom}}|{{milis.formatCustom}}")
                        .render(data));

        assertEquals(
                StringUtils.repeat(expectedDate, "|", 3),
                engine.compileMustache("date_only",
                        "{{calendar.formatDate}}|{{date.formatDate}}|{{milis.formatDate}}")
                        .render(data));
    }

    @Test(expected=IllegalStateException.class)
    public void testMultipleInit() {

        DateTimeFormatResolver resolver = new DateTimeFormatResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        resolver.init(null);
    }

}
