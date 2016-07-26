package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class FormatHelperTest extends AbstractTest {

    @Test
    public void testInterpolation() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addFmt().build())
                .build();
        assertEquals("Hello me!", engine
                .compileMustache("fmt_helper01", "{{fmt 'Hello %s!' 'me'}}")
                .render(null));
        assertEquals(" d  c  b  a",
                engine.compileMustache("fmt_helper02",
                        "{{fmt '%4$2s %3$2s %2$2s %1$2s' 'a' 'b' 'c' 'd'}}")
                        .render(null));
        assertEquals("Tuesday",
                engine.compileMustache("fmt_helper03",
                        "{{fmt '%tA' this locale='en'}}")
                        .render(LocalDateTime.of(2016, 7, 26, 12, 0)));

    }

    @Test
    public void testValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addFmt().build())
                .build();

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(new Runnable() {
                    public void run() {
                        engine.compileMustache("fmt_helper_validation01",
                                "{{#fmt}}{{/fmt}}");
                    }
                }).check(new Runnable() {
                    public void run() {
                        engine.compileMustache("fmt_helper_validation02",
                                "{{fmt}}");
                    }
                });
    }

}
