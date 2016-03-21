package org.trimou.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class IOUtilsTest {

    @Test
    public void testToString() throws IOException {
        assertEquals("foo", IOUtils.toString(new StringReader("foo")));
        assertEquals("foo", IOUtils.toString(new StringReader("foo"), 1));
    }

}
