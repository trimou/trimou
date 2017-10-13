package org.trimou.el;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ELSetHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        assertEquals("foo:bar (6)", engine.compileMustache(
                "{{#set couple='[item1,item2]' length='item1.length() + item2.length()'}}{{couple.0}}:{{couple.1}} ({{length}}){{/set}}")
                .render(ImmutableMap.of("item1", "foo", "item2", "bar")));
    }

}
