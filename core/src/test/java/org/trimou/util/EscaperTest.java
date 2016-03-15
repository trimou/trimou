package org.trimou.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class EscaperTest {

    @Test
    public void testEscaping() throws IOException {
        Escaper escaper = Escaper.builder().add('a', "aaa").build();
        assertEquals("aaa", escaper.escape("a"));
        assertEquals("b", escaper.escape("b"));
        StringBuilder builder = new StringBuilder();
        escaper.escape("ba", builder);
        assertEquals("baaa", builder.toString());
    }

}
