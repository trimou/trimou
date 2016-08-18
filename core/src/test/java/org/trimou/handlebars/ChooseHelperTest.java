package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ChooseHelperTest extends AbstractTest {

    @Test
    public void testChooseHelper() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addChoose().build())
                .build();

        assertEquals("transient", engine.compileMustache("choose_helper1",
                "{{#choose}}" + "{{#when up.isPersistent}}persistent{{/when}}"
                        + "{{#otherwise}}transient{{/otherwise}}"
                        + "{{/choose}}")
                .render(new Hammer()));
        assertEquals("P", engine.compileMustache("choose_helper2",
                "{{#choose}}" + "{{#when up}}P{{/when}}"
                        + "{{#otherwise}}T{{/otherwise}}" + "{{/choose}}")
                .render("not a falsy"));
        assertEquals("2", engine.compileMustache("choose_helper3",
                "{{#choose}}" + "{{#when up.null}}1{{/when}}"
                        + "{{#when up.age}}2{{/when}}"
                        + "{{#otherwise}}3{{/otherwise}}" + "{{/choose}}")
                .render(new Hammer()));

    }

    @Test
    public void testChooseHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addChoose().build())
                .build();

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> engine.compileMustache(
                        "choose_helper_validation01", "{{choose}}"))
                .check(() -> engine.compileMustache(
                        "choose_helper_validation02",
                        "{{#choose}}{{when \"true\"}}{{/choose}}"))
                .check(() -> engine.compileMustache(
                        "choose_helper_validation03",
                        "{{#choose}}{{#when \"true\"}}{{/when}}{{otherwise \"foo\"}}{{/choose}}"));

    }

    @Test
    public void testChooseHelperInvalidFlow() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty().addSet().addChoose().build())
                .build();

        MustacheExceptionAssert
                .expect(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS)
                .check(() -> engine
                        .compileMustache("choose_helper_invalid_flow01",
                                "{{#choose}}{{#set name=\"bar\"}}{{#when \"foo\"}}{{/when}}{{/set}}{{/choose}}")
                        .render("foo"));
    }

}
