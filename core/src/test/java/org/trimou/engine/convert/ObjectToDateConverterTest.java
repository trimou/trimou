package org.trimou.engine.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * t
 *
 * @author Martin Kouba
 */
public class ObjectToDateConverterTest {

    @Test
    public void testConverter() {
        String pattern = "yyyy.MM.dd HH:mm:ss.SSS";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        ObjectToDateConverter converter = new ObjectToDateConverter(pattern);
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        assertNull(converter.convert(BigDecimal.ONE));
        assertEquals(now, converter.convert(now));
        assertEquals(now, converter.convert(now.getTime()));
        assertEquals(cal.getTime(), converter.convert(cal));
        assertEquals(now, converter.convert(format.format(now)));
    }

}
