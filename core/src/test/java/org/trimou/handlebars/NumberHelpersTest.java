package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.trimou.AssertUtil.assertCompilationFails;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.NumericExpressionHelper.Operator;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableSet;

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
        assertEquals("even",
                engine.compileMustache("isEven_value",
                        "{{#this}}{{isEven iterIndex \"even\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));

        assertEquals("oddevenodd",
                engine.compileMustache("isEven_value_else",
                        "{{#this}}{{isEven iterIndex \"even\" \"odd\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));
        assertEquals("",
                engine.compileMustache("isEven_section",
                        "{{#isEven this}}even{{/isEven}}")
                        .render(3));
        assertEquals("even",
                engine.compileMustache("isEven_section",
                        "{{#isEven this}}even{{/isEven}}")
                        .render(2));
        assertCompilationFails(engine, "isEven_fail", "{{isEven}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testIsOdd() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addIsOdd().build())
                .build();
        assertEquals("oddodd",
                engine.compileMustache("isOdd_value",
                        "{{#this}}{{isOdd iterIndex \"odd\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));
        assertEquals("oddevenodd",
                engine.compileMustache("isOdd_value",
                        "{{#this}}{{isOdd iterIndex \"odd\" \"even\"}}{{/this}}")
                        .render(new String[] { "1", "2", "3" }));
        assertEquals("",
                engine.compileMustache("isOdd_section",
                        "{{#isOdd this}}odd{{/isOdd}}")
                        .render(4));
        assertEquals("odd",
                engine.compileMustache("isOdd_section",
                        "{{#isOdd this}}odd{{/isOdd}}")
                        .render(9));
        assertCompilationFails(engine, "isOdd_fail", "{{isOdd}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testNumericExpressionHelper() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addGlobalData("operator", "lt")
                .addGlobalData("longValuesSet", ImmutableSet.of(10l, 12l))
                .addGlobalData("longValuesArray", new Long[] { 10l, 12l })
                .registerHelpers(HelpersBuilder.empty().addNumExpr().build())
                .build();
        Map<String, Object> data = ImmutableMap.<String, Object> of("val1",
                2, "val2", 1, "val3",
                10L, "val4", BigDecimal.ZERO, "val5",
                BigInteger.ONE);
        assertEquals("true", engine
                .compileMustache("number_pos1", "{{numExpr this op='pos'}}")
                .render(1L));
        assertEquals("", engine
                .compileMustache("number_pos2", "{{numExpr this op='pos'}}")
                .render(-1L));
        assertEquals("true",
                engine.compileMustache("number_pos3", "{{numExpr 10 op='pos'}}")
                        .render(null));

        assertEquals("", engine
                .compileMustache("number_neg1", "{{numExpr this op='neg'}}")
                .render(1L));
        assertEquals("yes",
                engine.compileMustache("number_neg2",
                        "{{numExpr this op='neg' out='yes'}}")
                        .render(-1L));
        assertEquals("true",
                engine.compileMustache("number_eq1", "{{numExpr this '1'}}")
                        .render(1L));
        assertEquals("One!",
                engine.compileMustache("number_eq2",
                        "{{numExpr this '1' out='One!'}}")
                        .render(1L));
        assertEquals("yes",
                engine.compileMustache("number_eq3",
                        "{{#numExpr this '1' op='eq'}}yes{{/numExpr}}")
                        .render(1L));
        assertEquals("Not equal!",
                engine.compileMustache("number_neq1",
                        "{{numExpr -2 +2 op='neq' out='Not equal!'}}")
                        .render(1L));
        assertEquals("yes",
                engine.compileMustache("number_gt1",
                        "{{#numExpr val1 val2 op='gt'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_gt2",
                        "{{#numExpr val1 '0.1' op='gt'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_gt3",
                        "{{#numExpr '0.1' val4 op='gt'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_lt1",
                        "{{#numExpr val2 val1 op='lt'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_ge1",
                        "{{#numExpr val3 '10' op='ge'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("",
                engine.compileMustache("number_ge2",
                        "{{#numExpr val2 val3 op='ge'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_ge3",
                        "{{#numExpr this 5 op='ge'}}yes{{/numExpr}}")
                        .render(10));
        assertEquals("yes",
                engine.compileMustache("number_le1",
                        "{{#numExpr val2 val3 op='le'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_lt1",
                        "{{#numExpr val1 '2' op='le'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("",
                engine.compileMustache("number_lt2",
                        "{{#numExpr val1 '0' op='le'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("",
                engine.compileMustache("number_in1",
                        "{{#numExpr val1 '0' '5' val3 op='in'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_in2",
                        "{{#numExpr val1 '0' '5' val1 op='in'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_in3",
                        "{{#numExpr 10l longValuesSet op='in'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_nin1",
                        "{{#numExpr val1 '0' '5' op='nin'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("",
                engine.compileMustache("number_nin2",
                        "{{#numExpr '0' '5' '0' op='nin'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_nin3",
                        "{{#numExpr 0 longValuesArray op='nin'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_unknown_op",
                        "{{#numExpr val1 2 op='unknown!'}}yes{{/numExpr}}")
                        .render(data));
        assertEquals(
                "yes", engine
                        .compileMustache("number_default_op",
                                "{{#numExpr val1 2}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_dynamic_op",
                        "{{#numExpr val1 3 op=operator}}yes{{/numExpr}}")
                        .render(data));
        assertEquals("yes",
                engine.compileMustache("number_biginteger_val",
                        "{{#numExpr val5 val4 op='gt'}}yes{{/numExpr}}")
                        .render(data));
        MustacheExceptionAssert
                .expect(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS)
                .check(() -> engine
                        .compileMustache("number_invalid_val",
                                "{{#numExpr this op='neg'}}yes{{/numExpr}}")
                        .render(new ArrayList<>()));
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> engine.compileMustache("number_invalid_params",
                        "{{#numExpr this op='eq'}}yes{{/numExpr}}"));
        MustacheExceptionAssert
                .expect(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS)
                .check(() -> engine
                        .compileMustache("number_invalid_val",
                                "{{#numExpr this op=operator}}yes{{/numExpr}}")
                        .render(new ArrayList<>()));
    }

    @Test
    public void testNumericExpressionHelperDefaultOperator() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("gt", new NumericExpressionHelper(Operator.GT))
                .build();
        assertEquals(
                "foo", engine
                        .compileMustache("number_default_op",
                                "{{#gt this 10}}foo{{/gt}}")
                        .render(11L));
        assertEquals(
                "", engine
                        .compileMustache("number_default_op",
                                "{{#gt this 10}}foo{{/gt}}")
                        .render(10L));
    }

    @Test
    public void testNumericExpressionHelperInstanceForEachOperator() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(
                        NumericExpressionHelper.forEachOperator().build())
                .build();
        assertEquals(
                "foo", engine
                        .compileMustache("number_default_op1",
                                "{{#gt this 10}}foo{{/gt}}")
                        .render(11L));
        assertEquals(
                "foo", engine
                        .compileMustache("number_default_op2",
                                "{{#eq this 10}}foo{{/eq}}")
                        .render(10L));
        assertEquals("yes",
                engine.compileMustache("number_nin1",
                        "{{#nin val1 '0' '5'}}yes{{/nin}}")
                        .render(ImmutableMap.<String, Object> of("val1",
                                2, "val2", 1,
                                "val3", 10L, "val4",
                                BigDecimal.ZERO, "val5", BigInteger.ONE)));
    }

}
