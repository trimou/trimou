package org.trimou.jdk8.lambda;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.lambda.Lambda.InputType;

/**
 *
 * @author Martin Kouba
 */
public class SimpleLambdaTest {

    @Test
    public void testSimpleHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addGlobalData("lc",
                        SimpleLambdas.invoke((t) -> t.toLowerCase()))
                .addGlobalData("lc-builder",
                        SimpleLambdas.builder().inputType(InputType.PROCESSED)
                                .invoke((t) -> t.toUpperCase() + " {{foo}}")
                                .build())
                .addGlobalData("foo", "hello").build();
        assertEquals("hello ok", engine
                .compileMustache("simple_lambda_01", "{{#lc}}{{foo}} OK{{/lc}}")
                .render(null));
        assertEquals("HELLO ME! {{foo}}",
                engine.compileMustache("simple_lambda_02",
                        "{{#lc-builder}}{{foo}} me!{{/lc-builder}}")
                .render(null));
    }

}