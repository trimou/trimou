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

}
