package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class NumberHelpersTest extends AbstractTest {

    @Test
    public void testIsEven() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("isEven", NumberHelpers.IS_EVEN_HELPER).build();
        assertEquals(
                "even",
                engine.compileMustache("isEvent",
                        "{{#this}}{{isEven iterIndex \"even\"}}{{/this}}")
                        .render(new String[] { "0", "1", "2" }));
    }

}
