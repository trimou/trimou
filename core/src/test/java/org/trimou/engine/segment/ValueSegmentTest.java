package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.interpolation.ThrowingExceptionMissingValueHandler;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.lambda.InputProcessingLambda;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ValueSegmentTest extends AbstractEngineTest {

    @Before
    public void buildEngine() {
    }

    @SuppressWarnings("deprecation")
    // EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM used intentionally to
    // test backwards compatibility
    @Test
    public void testNoValueProblem() {
        try {
            MustacheEngineBuilder
                    .newBuilder()
                    .setProperty(
                            EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM,
                            true).build()
                    .compileMustache("value_segment_problem", "{{foo}}")
                    .render(null);
        } catch (MustacheException e) {
            if (!e.getCode().equals(MustacheProblem.RENDER_NO_VALUE)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testLambda() {
        Lambda lambda = new InputProcessingLambda() {
            @Override
            public String invoke(String text) {
                assertNull(text);
                return "ok";
            }

            @Override
            public boolean isReturnValueInterpolated() {
                return true;
            }
        };
        assertEquals(
                "ok",
                MustacheEngineBuilder
                        .newBuilder()
                        .build()
                        .compileMustache("value_segment_lambda", "{{lambda}}")
                        .render(ImmutableMap.<String, Object> of("lambda",
                                lambda)));
    }

    @Test
    public void testValueEscaping() {
        String shouldBeEscaped = "<&>";
        assertEquals(
                "&lt;&amp;&gt;",
                MustacheEngineBuilder
                        .newBuilder()
                        .build()
                        .compileMustache("value_escaping", "{{foo}}")
                        .render(ImmutableMap.<String, Object> of("foo",
                                shouldBeEscaped)));
        assertEquals(
                shouldBeEscaped,
                MustacheEngineBuilder
                        .newBuilder()
                        .setProperty(
                                EngineConfigurationKey.SKIP_VALUE_ESCAPING,
                                true)
                        .build()
                        .compileMustache("skip_value_escaping", "{{foo}}")
                        .render(ImmutableMap.<String, Object> of("foo",
                                shouldBeEscaped)));
    }

    @Test
    public void testMissingValueHandlerUsedForLambda() {
        Lambda lambda = new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return null;
            }
        };
        try {
            MustacheEngineBuilder
                    .newBuilder()
                    .setMissingValueHandler(
                            new ThrowingExceptionMissingValueHandler())
                    .build()
                    .compileMustache("value_lambda_missing_value_handler",
                            "{{this}}").render(lambda);
            fail();
        } catch (MustacheException e) {
            if (!MustacheProblem.RENDER_NO_VALUE.equals(e.getCode())) {
                fail("Unexpected problem");
            }
        }
    }

}
