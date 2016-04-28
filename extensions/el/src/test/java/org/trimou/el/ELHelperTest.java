package org.trimou.el;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ELHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        assertEquals("true",
                engine.compileMustache("elhelper_01", "{{el 'this eq this'}}")
                        .render(true));
        assertEquals("yes", engine.compileMustache("elhelper_02",
                "{{#el 'this gt 10'}}yes{{/el}}").render(10));
        assertEquals("yes", engine
                .compileMustache("elhelper_03", "{{#el 'this < 1'}}yes{{/el}}")
                .render(0));
        assertEquals("10",
                engine.compileMustache("elhelper_04", "{{el 'this.age'}}")
                        .render(new Hammer(10)));
        assertEquals("1two",
                engine.compileMustache("elhelper_05",
                        "{{#el '[1, \"two\"]'}}{{#each this}}{{this}}{{/each}}{{/el}}")
                        .render(null));
        assertEquals("123",
                engine.compileMustache("elhelper_06",
                        "{{#el '{\"one\":1, \"two\":2, \"three\":3}'}}{{one}}{{two}}{{three}}{{/el}}")
                        .render(null));
        assertEquals(
                "no", engine
                        .compileMustache("elhelper_07",
                                "{{el 'this ? \"yes\" : \"no\"'}}")
                        .render(false));
        assertEquals("10",
                engine.compileMustache("elhelper_08",
                        "{{#el 'foo < bar ? foo : bar'}}{{this}}{{/el}}")
                        .render(ImmutableMap.of("foo", 10, "bar", 20)));
    }

}
