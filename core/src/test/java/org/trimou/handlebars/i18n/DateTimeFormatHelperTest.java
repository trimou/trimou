package org.trimou.handlebars.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.FixedLocaleSupport;
import org.trimou.util.ImmutableMap;
import org.trimou.util.Strings;

/**
 * @author Martin Kouba
 */
public class DateTimeFormatHelperTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
        engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("formatTime", new DateTimeFormatHelper())
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .build();
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

        Map<String, Object> data = ImmutableMap.of("calendar", day, "date", day.getTime(), "milis", milis);

        assertEquals(
                Strings.repeat(expectedMedium, 3, "|"),
                engine.compileMustache(
                        "date_time_helper_medium",
                        "{{formatTime calendar style=\"medium\"}}|{{formatTime date style=\"medium\"}}|{{formatTime milis style=\"medium\"}}")
                        .render(data));
        assertEquals(
                Strings.repeat(expectedShort, 3, "|"),
                engine.compileMustache(
                        "date_time_helper_short",
                        "{{formatTime calendar style=\"short\"}}|{{formatTime date style=\"short\"}}|{{formatTime milis style=\"short\"}}")
                        .render(data));
        assertEquals(
                Strings.repeat(expectedCustom, 3, "|"),
                engine.compileMustache(
                        "date_time_helper_custom",
                        "{{formatTime calendar pattern=\"DD-MM-yyyy HH:mm\"}}|{{formatTime date pattern=\"DD-MM-yyyy HH:mm\"}}|{{formatTime milis pattern=\"DD-MM-yyyy HH:mm\"}}")
                        .render(data));
    }

}