package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.trimou.AssertUtil.assertCompilationFails;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class NumberHelpersTest extends AbstractTest {

    @Test
    public void testIsEven() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addIsEven().build())
                .build();
        assertEquals(
                "even",
                engine.compileMustache("isEven_value",
                        "{{#this}}{{isEven iterIndex \"even\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));

        assertEquals(
                "oddevenodd",
                engine.compileMustache("isEven_value_else",
                        "{{#this}}{{isEven iterIndex \"even\" \"odd\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));
        assertEquals(
                "",
                engine.compileMustache("isEven_section",
                        "{{#isEven this}}even{{/isEven}}").render(
                        Integer.valueOf(3)));
        assertEquals(
                "even",
                engine.compileMustache("isEven_section",
                        "{{#isEven this}}even{{/isEven}}").render(
                        Integer.valueOf(2)));
        assertCompilationFails(engine, "isEven_fail", "{{isEven}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testIsOdd() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addIsOdd().build())
                .build();
        assertEquals(
                "oddodd",
                engine.compileMustache("isOdd_value",
                        "{{#this}}{{isOdd iterIndex \"odd\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));
        assertEquals(
                "oddevenodd",
                engine.compileMustache("isOdd_value",
                        "{{#this}}{{isOdd iterIndex \"odd\" \"even\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));
        assertEquals(
                "",
                engine.compileMustache("isOdd_section",
                        "{{#isOdd this}}odd{{/isOdd}}").render(
                        Integer.valueOf(4)));
        assertEquals(
                "odd",
                engine.compileMustache("isOdd_section",
                        "{{#isOdd this}}odd{{/isOdd}}").render(
                        Integer.valueOf(9)));
        assertCompilationFails(engine, "isOdd_fail", "{{isOdd}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

}
