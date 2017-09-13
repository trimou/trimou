package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;
import org.trimou.util.ImmutableList;
import org.trimou.util.ImmutableMap;

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
        assertEquals("11",
                engine.compileMustache("each_skipif_3", "{{#each this apply='skipIf:empty'}}{{length}}{{/each}}")
                        .render(ImmutableList.of(null, "", "1", "2")));
    }

    @Test
    public void testMap() {
        assertEquals("332", engine.compileMustache("each_map_1", "{{#each this apply='map:length'}}{{this}}{{/each}}")
                .render(ImmutableList.of("foo", "bar", "uf")));
        assertEquals("1020",
                engine.compileMustache("each_map_2", "{{#each this apply='map:age.longValue'}}{{this}}{{/each}}")
                        .render(ImmutableList.of(new Hammer(), new Hammer(20))));
    }

    @Test
    public void testIterateOverPeek() {
        assertEquals("123", engine.compileMustache("each_peek_1", "{{#with this}}{{#each}}{{this}}{{/each}}{{/with}}")
                .render(ImmutableList.of("1", "2", "3")));
    }

    @Test
    public void testOmitMeta() {
        assertEquals("123",
                engine.compileMustache("each_omitMeta_1", "{{#each omitMeta=true}}{{iterIndex}}{{this}}{{/each}}")
                        .render(ImmutableList.of("1", "2", "3")));
        assertEquals("123foo", engine
                .compileMustache("each_omitMeta_2", "{{#each list array omitMeta=true}}{{iterIndex}}{{this}}{{/each}}")
                .render(ImmutableMap.of("list", ImmutableList.of("1", "2", "3"), "array", new String[] { "foo" })));
    }

    @Test
    public void testStream() {
        assertEquals("13", engine.compileMustache("each_stream_1", "{{#each this}}{{iterIndex}}{{this}}{{/each}}")
                .render(ImmutableList.of("1", "2", "3").stream().filter((e) -> !e.equals("2"))));
        assertEquals("13foo",
                engine.compileMustache("each_omitMeta_2", "{{#each list array}}{{iterIndex}}{{this}}{{/each}}")
                        .render(ImmutableMap.of("list",
                                ImmutableList.of("1", "2", "3").stream().filter((e) -> !e.equals("2")), "array",
                                new String[] { "foo" })));
    }

    @Test
    public void testIterator() {
        assertEquals("123", engine.compileMustache("each_stream_1", "{{#each this}}{{iterIndex}}{{this}}{{/each}}")
                .render(ImmutableList.of("1", "2", "3").iterator()));
    }

    @Test
    public void testSpliterator() {
        assertEquals("123", engine.compileMustache("each_stream_1", "{{#each this}}{{iterIndex}}{{this}}{{/each}}")
                .render(ImmutableList.of("1", "2", "3").spliterator()));
    }

}
