package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.interpolation.NoOpMissingValueHandler;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class AlternativesHelperTest extends AbstractTest {

    @Test
    public void testInterpolation() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addAlt().addMin()
                        .addMax().build())
                .setMissingValueHandler(new NoOpMissingValueHandler() {
                    @Override
                    public Object handle(MustacheTagInfo tagInfo) {
                        return "---";
                    }
                }).build();
        assertEquals("Joe",
                engine.compileMustache("alt_helper01", "{{alt name 'Joe'}}")
                        .render(null));
        assertEquals("Ed",
                engine.compileMustache("alt_helper02", "{{alt this 'Joe'}}")
                        .render("Ed"));
        assertEquals("---", engine
                .compileMustache("alt_helper03", "{{alt this}}").render(null));
        assertEquals("Joe",
                engine.compileMustache("alt_helper04", "{{alt 'Joe' this}}")
                        .render("Ed"));
        assertEquals("Ed",
                engine.compileMustache("alt_helper05", "{{alt '' this}}")
                        .render("Ed"));
        assertEquals("0", engine
                .compileMustache("min_helper01", "{{min '1' 10 30l this}}")
                .render(BigDecimal.ZERO));
        assertEquals("30",
                engine.compileMustache("max_helper01", "{{max 1 10 30l this}}")
                        .render(BigDecimal.ZERO));
        assertEquals(
                "Me", engine
                        .compileMustache("alt_helper01",
                                "{{#alt 'Me' 'Joe'}}{{this}}{{/alt}}")
                        .render(null));
    }

    @Test
    public void testValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addAlt().build())
                .build();

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> engine.compileMustache("alt_helper_validation01",
                        "{{#alt}}{{/alt}}"))
                .check(() -> engine.compileMustache("alt_helper_validation02",
                        "{{alt}}"));
    }

}
