package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.trimou.AbstractTest;

/**
 *
 * @author Martin Kouba
 */
public class MatchingSectionHelperTest extends AbstractTest {

    @Test
    public void testElsePattern() {
        assertPattern("{1}", "{", "}", 1, "FOUND");
        assertPattern("{foo } and {bar}", "{", "}", 2, "FOUND and FOUND");
        assertPattern("$foo $ and {bar}", "$", "$", 1, "FOUND and {bar}");
        assertPattern("$foo$ $", "$", "$", 1, "FOUND $");

    }

    private void assertPattern(String text, String start, String end,
            int expectedOccurences, String expectedResult) {
        Pattern pattern = MatchingSectionHelper.initElsePattern(start, end);
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();
        int occurences = 0;
        while (matcher.find()) {
            matcher.appendReplacement(result, "FOUND");
            occurences++;
        }
        matcher.appendTail(result);
        assertEquals(expectedOccurences, occurences);
        assertEquals(expectedResult, result.toString());
    }
}
