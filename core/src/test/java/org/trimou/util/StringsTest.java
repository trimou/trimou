package org.trimou.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class StringsTest {

    @Test
    public void testReplace() {
        assertEquals("foobar", Strings.replace("foo:bar", ":", ""));
    }

    @Test
    public void testContainsWhitespace() {
        assertTrue(Strings.containsWhitespace("foo bar"));
        assertTrue(Strings
                .containsWhitespace(System.getProperty("line.separator")));
        assertFalse(Strings.containsWhitespace("foobar"));
    }

    @Test
    public void testContainsOnlyWhitespace() {
        assertTrue(Strings.containsOnlyWhitespace("  "));
        assertTrue(Strings.containsOnlyWhitespace(
                "  " + System.getProperty("line.separator")));
        assertFalse(Strings.containsOnlyWhitespace(" !"));
    }

    @Test
    public void testContainsOnlyDigits() {
        assertTrue(Strings.containsOnlyDigits("123"));
        assertFalse(Strings.containsOnlyDigits("5!"));
    }

    @Test
    public void testRepeat() {
        assertEquals("foo,foo", Strings.repeat("foo", 2, ","));
        assertEquals("ooo", Strings.repeat("o", 3, ""));
        assertEquals("o", Strings.repeat("o", -3, ""));
        assertEquals("", Strings.repeat("ouch", 0, ""));
    }

    @Test
    public void testSubstringAfter() {
        assertEquals("o", Strings.substringAfter("foo", "o"));
        assertEquals("", Strings.substringAfter("foo", "a"));

    }


}
