package org.trimou.util;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class PatternsTest {

    @Test
    public void testMustacheTagPattern() {
        String text = "{{foo }} and {{#bar}}...{{/bar}}";
        Pattern tagPattern = Patterns
                .newMustacheTagPattern(MustacheEngineBuilder.newBuilder()
                        .build().getConfiguration());
        Matcher matcher = tagPattern.matcher(text);
        StringBuffer result = new StringBuffer();
        int occurences = 0;
        while (matcher.find()) {
            System.out.println(matcher.group(0));
            matcher.appendReplacement(result, "FOUND");
            occurences++;
        }
        matcher.appendTail(result);
        assertEquals(3, occurences);
        assertEquals("FOUND and FOUND...FOUND", result.toString());
    }

    @Test
    public void testSetDelimitersPattern() {
        String set1 = "<% %>";
        String set2 = "[ ]";
        String set3 = "= =";
        Pattern setDelimitersPattern = Patterns
                .newSetDelimitersContentPattern();
        Matcher set1Matcher = setDelimitersPattern.matcher(set1);
        assertEquals(true, set1Matcher.find());
        assertEquals("<%", set1Matcher.group(1));
        assertEquals("%>", set1Matcher.group(3));
        Matcher set2Matcher = setDelimitersPattern.matcher(set2);
        assertEquals(true, set2Matcher.find());
        assertEquals("[", set2Matcher.group(1));
        assertEquals("]", set2Matcher.group(3));
        Matcher set3Matcher = setDelimitersPattern.matcher(set3);
        assertEquals(true, set3Matcher.find());
        assertEquals("=", set3Matcher.group(1));
        assertEquals("=", set3Matcher.group(3));
    }

}