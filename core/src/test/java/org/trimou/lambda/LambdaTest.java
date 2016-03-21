package org.trimou.lambda;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.segment.SectionSegmentTest;
import org.trimou.engine.segment.ValueSegmentTest;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 * @see SectionSegmentTest#testLambdas()
 * @see ValueSegmentTest#testLambda()
 */
public class LambdaTest extends AbstractEngineTest {

    @Test
    public void testReturnValueInterpolated() {
        Lambda lambda = new InputProcessingLambda() {
            @Override
            public String invoke(String text) {
                return "{{foo}}|{{foo}}";
            }

            @Override
            public boolean isReturnValueInterpolated() {
                return true;
            }
        };
        assertEquals(
                "true|true",
                engine.compileMustache("lambda_return_interpolated",
                        "{{#lambda}}Hello{{/lambda}}").render(
                        ImmutableMap.of("foo", "true", "lambda", lambda)));
        lambda = (input) -> "{{foo}}|{{foo}}";
        assertEquals(
                "true|true",
                engine.compileMustache("lambda_return_interpolated2",
                        "{{#lambda}}Hello{{/lambda}}").render(
                        ImmutableMap.of("foo", "true", "lambda", lambda)));
    }

}
