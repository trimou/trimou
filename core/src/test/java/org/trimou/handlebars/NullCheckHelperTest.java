package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class NullCheckHelperTest extends AbstractTest {

    @Test
    public void testNullCheckHelper() {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty()
                                .addIsNull()
                                .addIsNotNull()
                                .build()).build();

        assertEquals(
                "NULL|NOTNULL",
                engine.compileMustache(
                        "nullcheck_helper1",
                        "{{#isNull this.getNull}}NULL{{/isNull}}|{{#isNotNull this.toString}}NOTNULL{{/isNotNull}}")
                        .render(new Hammer()));
        assertEquals(
                "NULL",
                engine.compileMustache(
                        "nullcheck_helper2",
                        "{{#isNull this.getNull this.null}}NULL{{/isNull}}")
                        .render(new Hammer()));
        assertEquals(
                "NULL",
                engine.compileMustache(
                        "nullcheck_helper3",
                        "{{#isNull this.getNull this.age logic=\"or\"}}NULL{{/isNull}}")
                        .render(new Hammer()));
        assertEquals(
                "NOTNULL",
                engine.compileMustache(
                        "nullcheck_helper3",
                        "{{#isNotNull this.getNull this.age logic=\"or\"}}NOTNULL{{/isNotNull}}")
                        .render(new Hammer()));
        assertEquals(
                "ISNULL",
                engine.compileMustache(
                        "nullcheck_helper4",
                        "{{#isNotNull this.getNull else='ISNULL'}}NOTNULL{{/isNotNull}}")
                        .render(new Hammer()));
    }

}
