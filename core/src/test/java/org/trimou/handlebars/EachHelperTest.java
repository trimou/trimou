package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;
import org.trimou.util.ImmutableList;

/**
 * @author Martin Kouba
 */
public class EachHelperTest extends AbstractEngineTest {

    @Test
    public void testSkipIfNull() {
        assertEquals("foobaz",
                engine.compileMustache("each_skipifnull", "{{#each this apply='skipIfNull'}}{{this}}{{/each}}")
                        .render(ImmutableList.of("foo", null, "baz")));
    }

    @Test
    public void testSkipUnless() {
        assertEquals("true",
                engine.compileMustache("each_skipunless_1",
                        "{{#each this apply='skipUnless:booleanValue'}}{{this}}{{/each}}")
                        .render(ImmutableList.of("foo", null, Boolean.FALSE, Boolean.TRUE)));
        assertEquals("=",
                engine.compileMustache("each_skipunless_2",
                        "={{#each this apply='skipUnless:map.isEmpty'}}{{this}}{{/each}}")
                        .render(ImmutableList.of(new Hammer())));
    }

    @Test
    public void testSkipIf() {
        assertEquals("foofalse",
                engine.compileMustache("each_skipif_1", "{{#each this apply='skipIf:isCool'}}{{this}}{{/each}}")
                        .render(ImmutableList.of("foo", new Hammer(), Boolean.FALSE)));
        assertEquals("=10",
                engine.compileMustache("each_skipif_2", "={{#each this apply='skipIf:map.isEmpty'}}{{age}}{{/each}}")
                        .render(ImmutableList.of(new Hammer())));
    }

    @Test
    public void testMap() {
        assertEquals("332", engine.compileMustache("each_map_1", "{{#each this apply='map:length'}}{{this}}{{/each}}")
                .render(ImmutableList.of("foo", "bar", "uf")));
        assertEquals("1020", engine.compileMustache("each_map_2", "{{#each this apply='map:age.longValue'}}{{this}}{{/each}}")
                .render(ImmutableList.of(new Hammer(), new Hammer(20))));
    }

}
