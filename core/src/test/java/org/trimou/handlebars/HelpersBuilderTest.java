package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class HelpersBuilderTest {

    @Test
    public void testSimpleAdd() {
        Map<String, Helper> helpers = HelpersBuilder.empty()
                .add("foo", new WithHelper()).build();
        assertEquals(1, helpers.size());
        assertTrue(helpers.containsKey("foo"));
    }

    @Test
    public void testBuiltin() {
        Map<String, Helper> helpers = HelpersBuilder.builtin().build();
        assertEquals(5, helpers.size());
        assertTrue(helpers.containsKey(HelpersBuilder.EACH));
        assertTrue(helpers.containsKey(HelpersBuilder.IF));
        assertTrue(helpers.containsKey(HelpersBuilder.IS));
        assertTrue(helpers.containsKey(HelpersBuilder.UNLESS));
        assertTrue(helpers.containsKey(HelpersBuilder.WITH));
    }

    @Test
    public void testExtra() {
        Map<String, Helper> helpers = HelpersBuilder.extra().build();
        assertEquals(23, helpers.size());
        assertTrue(helpers.containsKey(HelpersBuilder.EMBED));
        assertTrue(helpers.containsKey(HelpersBuilder.INCLUDE));
        assertTrue(helpers.containsKey(HelpersBuilder.IS_EQUAL));
        assertTrue(helpers.containsKey(HelpersBuilder.IS_NOT_EQUAL));
        assertTrue(helpers.containsKey(HelpersBuilder.IS_NULL));
        assertTrue(helpers.containsKey(HelpersBuilder.IS_NOT_NULL));
        assertTrue(helpers.containsKey(HelpersBuilder.SET));
        assertTrue(helpers.containsKey(HelpersBuilder.IS_EVEN));
        assertTrue(helpers.containsKey(HelpersBuilder.IS_ODD));
        assertTrue(helpers.containsKey(HelpersBuilder.SWITCH));
        assertTrue(helpers.containsKey(HelpersBuilder.CHOOSE));
        assertTrue(helpers.containsKey(HelpersBuilder.JOIN));
        assertTrue(helpers.containsKey(HelpersBuilder.EVAL));
        assertTrue(helpers.containsKey(HelpersBuilder.NUMERIC_EXPRESSION));
        assertTrue(helpers.containsKey(HelpersBuilder.ASYNC));
        assertTrue(helpers.containsKey(HelpersBuilder.INVOKE));
        assertTrue(helpers.containsKey(HelpersBuilder.ALT));
        assertTrue(helpers.containsKey(HelpersBuilder.MIN));
        assertTrue(helpers.containsKey(HelpersBuilder.MAX));
    }

}
