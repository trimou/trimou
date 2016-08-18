package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class SetHelperTest extends AbstractTest {

    @Test
    public void testSetHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addSet().build())
                .build();
        assertEquals("hellohello",
                engine.compileMustache("set_helper01",
                        "{{foo}}{{bar}}{{#set foo=\"hello\"}}{{foo}}{{bar}}{{#set bar=foo}}{{bar}}{{/set}}{{/set}}{{foo}}{{bar}}")
                        .render(null));
        assertEquals("helloping1",
                engine.compileMustache("set_helper02",
                        "{{#set foo=\"hello\" bar=\"ping\" qux=one}}{{foo}}{{bar}}{{qux}}{{/set}}")
                        .render(ImmutableMap.of("one", BigDecimal.ONE)));
    }

    @Test
    public void testSetHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addSet().build())
                .build();

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> engine.compileMustache("set_helper_validation01",
                        "{{#set}}{{/set}}"))
                .check(() -> engine.compileMustache("set_helper_validation02",
                        "{{#set \"foo\"}}{{/set}}"));
    }

}
