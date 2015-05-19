package org.trimou.jdk8.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.trimou.jdk8.convert.EnhancedObjectToDateConverter;

/**
 *
 *
 * @author Martin Kouba
 */
public class EnhancedObjectToDateConverterTest {

    @Test
    public void testConverter() {
        String pattern = "yyyy.MM.dd HH:mm:ss.SSS";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        EnhancedObjectToDateConverter converter = new EnhancedObjectToDateConverter(
                pattern);
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        LocalDateTime dateTime = LocalDateTime.ofInstant(now.toInstant(),
                ZoneId.systemDefault());
        LocalDate date = LocalDateTime.ofInstant(midnight.toInstant(),
                ZoneId.systemDefault()).toLocalDate();
        assertNull(converter.convert(BigDecimal.ONE));
        assertEquals(now, converter.convert(now));
        assertEquals(now, converter.convert(now.getTime()));
        assertEquals(cal.getTime(), converter.convert(cal));
        assertEquals(now, converter.convert(format.format(now)));
        assertEquals(now, converter.convert(dateTime));
        assertEquals(midnight.getTime(), converter.convert(date));
    }

}
