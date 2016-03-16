package org.trimou.jdk8.handlebars.i18n;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableMap;

/**
 * @author Martin Kouba
 */
public class TimeFormatHelperTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
        engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("formatTime", new TimeFormatHelper())
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
                }).build();
    }

    @Test
    public void testInterpolation() {

        Calendar day = day();
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

        Map<String, Object> data = ImmutableMap.<String, Object> of("calendar",
                day, "date", day.getTime(), "milis", milis, "localDateTime",
                LocalDateTime.ofInstant(day.toInstant(), TimeZone.getDefault()
                        .toZoneId()));

        assertEquals(
                Strings.repeat(expectedMedium, 4, "|"),
                engine.compileMustache(
                        "time_helper_medium",
                        "{{formatTime calendar style=\"medium\"}}|{{formatTime date style=\"medium\"}}|{{formatTime milis style=\"medium\"}}|{{formatTime localDateTime style=\"medium\"}}")
                        .render(data));
        assertEquals(
                Strings.repeat(expectedShort, 4, "|"),
                engine.compileMustache(
                        "time_helper_short",
                        "{{formatTime calendar style=\"short\"}}|{{formatTime date style=\"short\"}}|{{formatTime milis style=\"short\"}}|{{formatTime localDateTime style=\"short\"}}")
                        .render(data));
        assertEquals(
                Strings.repeat(expectedCustom, 4, "|"),
                engine.compileMustache(
                        "time_helper_custom",
                        "{{formatTime calendar pattern=\"DD-MM-yyyy HH:mm\"}}|{{formatTime date pattern=\"DD-MM-yyyy HH:mm\"}}|{{formatTime milis pattern=\"DD-MM-yyyy HH:mm\"}}|{{formatTime localDateTime pattern=\"DD-MM-yyyy HH:mm\"}}")
                        .render(data));
    }

    @Test
    public void testTimezone() {
        Calendar day = day();
        day.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));

        String expectedCustom = "01-01-2013 12:00";

        assertEquals(
                expectedCustom,
                engine.compileMustache("time_helper_timezone",
                        "{{formatTime day pattern=\"DD-MM-yyyy HH:mm\" timeZone=\"Europe/London\"}}")
                        .render(ImmutableMap.of("day", day)));
    }

    @Test
    public void testLocale() {

        Calendar day = day();

        String expectedCustom = "Led 01-01-2013";

        assertEquals(
                expectedCustom,
                engine.compileMustache("time_helper_locale",
                        "{{formatTime this pattern=\"MMM DD-MM-yyyy\" locale='cs'}}")
                        .render(day));

    }

    private Calendar day() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2013);
        day.set(Calendar.MONTH, 0);
        day.set(Calendar.DAY_OF_MONTH, 1);
        day.set(Calendar.HOUR_OF_DAY, 13);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day;
    }

}