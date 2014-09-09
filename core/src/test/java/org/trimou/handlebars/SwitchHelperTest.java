package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.ArchiveType;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class SwitchHelperTest extends AbstractTest {

    @Test
    public void testSwitchHelper() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addGlobalData("type", ArchiveType.class)
                .registerHelpers(HelpersBuilder.empty().addSwitch().build())
                .build();

        assertEquals(
                "WAR",
                engine.compileMustache(
                        "switch_helper1",
                        "{{#switch this}}"
                        + " {{#case type.WAR break=\"true\"}}WAR{{/case}}"
                        + " {{#case type.EAR}}EAR{{/case}}"
                        + " {{#case type.JAR}}JAR{{/case}}"
                        + " {{#default}}none{{/default}}"
                        + "{{/switch}}")
                        .render(new Hammer().getArchiveType()).trim());
        assertEquals(
                "baz",
                engine.compileMustache(
                        "switch_helper2",
                        "{{#switch this}}"
                        + "{{#case \"foo\"}}A{{/case}}"
                        + "{{#case \"bar\"}}B{{/case}}"
                        + "{{#default}}{{this.up}}{{/default}}"
                        + "{{/switch}}")
                        .render("baz").trim());
        assertEquals(
                "B",
                engine.compileMustache(
                        "switch_helper3",
                        "{{#switch this}}"
                        + "{{#case \"foo\"}}A{{/case}}"
                        + "{{#case \"bar\" break=\"true\"}}B{{/case}}"
                        + "{{#default}}C{{/default}}"
                        + "{{/switch}}")
                        .render("bar").trim());
        assertEquals(
                "ABC",
                engine.compileMustache(
                        "switch_helper4",
                        "{{#switch this}}"
                        + "{{#case \"foo\"}}A{{/case}}"
                        + "{{#case \"bar\"}}B{{/case}}"
                        + "{{#default}}C{{/default}}"
                        + "{{/switch}}")
                        .render("foo").trim());

    }

    @Test
    public void testSwitchHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addSwitch().build())
                .build();

        MustacheExceptionAssert
        .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
        .check(new Runnable() {
            public void run() {
                engine.compileMustache("switch_helper_validation01",
                        "{{switch}}");
            }
        }).check(new Runnable() {
            public void run() {
                engine.compileMustache("switch_helper_validation02",
                        "{{#switch}}{{case \"true\"}}{{/switch}}");
            }
        }).check(new Runnable() {
            public void run() {
                engine.compileMustache("switch_helper_validation03",
                        "{{#switch}}{{#case \"true\"}}{{/case}}{{default \"foo\"}}{{/switch}}");
            }
        });

    }

    @Test
    public void testSwitchHelperInvalidFlow() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addSet().addSwitch().build())
                .build();

        MustacheExceptionAssert
        .expect(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS)
        .check(new Runnable() {
            public void run() {
                engine.compileMustache("switch_helper_invalid_flow01",
                        "{{#switch}}{{#set name=\"bar\"}}{{#case \"foo\"}}{{/case}}{{/set}}{{/switch}}").render("foo");
            }
        });
    }

    @Test
    public void testSwitchHelperCaseIsBreakByDefault() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addSwitch(true).build())
                .build();

        assertEquals(
                "A",
                engine.compileMustache(
                        "switch_helper_break_by_default",
                        "{{#switch this}}"
                        + "{{#case \"foo\"}}A{{/case}}"
                        + "{{#case \"bar\"}}B{{/case}}"
                        + "{{#default}}C{{/default}}"
                        + "{{/switch}}")
                        .render("foo").trim());
    }

}
