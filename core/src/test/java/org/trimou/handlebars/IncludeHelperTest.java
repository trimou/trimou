package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class IncludeHelperTest extends AbstractTest {

    @Test
    public void testIncludeHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addInclude().build())
                .addTemplateLocator(new MapTemplateLocator(
                        ImmutableMap.of("template", "Hello!")))
                .build();
        assertEquals("Hello!",
                engine.compileMustache("include_helper01", "{{include this}}")
                        .render("template"));
        assertEquals("Hello!", engine
                .compileMustache("include_helper02", "{{include 'temp' this}}")
                .render("late"));
    }

    @Test
    public void testIncludeHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addInclude().build())
                .build();

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> engine.compileMustache(
                        "include_helper_validation01", "{{include}}"))
                .check(() -> engine.compileMustache("set_helper_validation02",
                        "{{#include \"foo\"}}{{/include}}"));
    }

}
