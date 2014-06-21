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
public class EqualsHelperTest extends AbstractTest {

    @Test
    public void testEqualsHelper() {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addGlobalData("archiveType", ArchiveType.class)
                .registerHelpers(
                        HelpersBuilder.empty().addIsEqual().addIsNotEqual()
                                .build()).build();

        assertEquals(
                "EQUALS",
                engine.compileMustache("equals_helper1",
                        "{{#isEq this.age this.age}}EQUALS{{/isEq}}").render(
                        new Hammer()));
        assertEquals(
                "EQUALS",
                engine.compileMustache("equals_helper2",
                        "{{#isEq this}}EQUALS{{/isEq}}").render(new Hammer()));
        assertEquals(
                "NOT_EQUALS",
                engine.compileMustache("equals_helper3",
                        "{{#isNotEq this.age}}NOT_EQUALS{{/isNotEq}}").render(
                        new Hammer()));
        assertEquals(
                "NOT_EQUALS",
                engine.compileMustache("equals_helper4",
                        "{{#isNotEq this.age this.toString}}NOT_EQUALS{{/isNotEq}}")
                        .render(new Hammer()));
        assertEquals("It's a WAR!", engine.compileMustache(
                "equals_helper5",
                "{{#with this.archiveType}}" + "{{#isEq archiveType.WAR}}"
                        + "It's a WAR!" + "{{/isEq}}"
                        + "{{#isEq archiveType.EAR}}" + "It's an EAR!"
                        + "{{/isEq}}" + "{{/with}}").render(new Hammer()));
    }

    @Test
    public void testEqualsHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addIsEqual().build())
                .build();

        MustacheExceptionAssert.expect(
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE).check(
                new Runnable() {
                    public void run() {
                        engine.compileMustache("equals_helper_validation01",
                                "{{#isEq}}{{/isEq}}");
                    }
                });
    }
}
