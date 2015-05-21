package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.trimou.AssertUtil.assertCompilationFails;

import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.ImmutableMap;

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

    @Test
    public void testNumericExpressionHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addNumExpr().build())
                .build();
        Map<String, Number> data = ImmutableMap.<String, Number> of("val1",
                Integer.valueOf(2), "val2", Integer.valueOf(1), "val3",
                Long.valueOf(10));
        assertEquals(
                "true",
                engine.compileMustache("number_pos1",
                        "{{numExpr this op='pos'}}").render(
                        Long.valueOf(1)));
        assertEquals(
                "",
                engine.compileMustache("number_pos2",
                        "{{numExpr this op='pos'}}").render(
                        Long.valueOf(-1)));
        assertEquals(
                "true",
                engine.compileMustache("number_pos3",
                        "{{numExpr 10 op='pos'}}").render(null));

        assertEquals(
                "",
                engine.compileMustache("number_neg1",
                        "{{numExpr this op='neg'}}").render(
                        Long.valueOf(1)));
        assertEquals(
                "yes",
                engine.compileMustache("number_neg2",
                        "{{numExpr this op='neg' out='yes'}}").render(
                        Long.valueOf(-1)));
        assertEquals("true",
                engine.compileMustache("number_eq1", "{{numExpr this '1'}}")
                        .render(Long.valueOf(1)));
        assertEquals(
                "One!",
                engine.compileMustache("number_eq2",
                        "{{numExpr this '1' out='One!'}}").render(
                        Long.valueOf(1)));
        assertEquals(
                "yes",
                engine.compileMustache("number_eq3",
                        "{{#numExpr this '1' op='eq'}}yes{{/numExpr}}").render(
                        Long.valueOf(1)));
        assertEquals(
                "Not equal!",
                engine.compileMustache("number_neq1",
                        "{{numExpr -2 +2 op='neq' out='Not equal!'}}").render(
                        Long.valueOf(1)));
        assertEquals(
                "yes",
                engine.compileMustache("number_gt1",
                        "{{#numExpr val1 val2 op='gt'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "yes",
                engine.compileMustache("number_gt2",
                        "{{#numExpr val1 '0.1' op='gt'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "yes",
                engine.compileMustache("number_ge1",
                        "{{#numExpr val3 '10' op='ge'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "",
                engine.compileMustache("number_ge2",
                        "{{#numExpr val2 val3 op='ge'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "yes",
                engine.compileMustache("number_ge3",
                        "{{#numExpr this 5 op='ge'}}yes{{/numExpr}}")
                        .render(10));
        assertEquals(
                "yes",
                engine.compileMustache("number_le1",
                        "{{#numExpr val2 val3 op='le'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "yes",
                engine.compileMustache("number_lt1",
                        "{{#numExpr val1 '2' op='le'}}yes{{/numExpr}}").render(
                        data));
        assertEquals(
                "",
                engine.compileMustache("number_lt2",
                        "{{#numExpr val1 '0' op='le'}}yes{{/numExpr}}").render(
                        data));
        assertEquals(
                "",
                engine.compileMustache("number_in1",
                        "{{#numExpr val1 '0' '5' val3 op='in'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "yes",
                engine.compileMustache("number_in2",
                        "{{#numExpr val1 '0' '5' val1 op='in'}}yes{{/numExpr}}")
                        .render(data));
    }

}
