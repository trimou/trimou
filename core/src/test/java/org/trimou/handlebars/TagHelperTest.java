package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

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
public class TagHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addTag().build()).build();
        assertEquals("{{foo}}", engine.compileMustache("tag_helper1", "{{tag 'foo'}}").render(null));
        assertEquals("{{#each items as=\"item\"}}{{this}}{{/each}}",
                engine.compileMustache("tag_helper2", "{{#tag 'each' 'items as=\"item\"'}}{{tag 'this'}}{{/tag}}")
                        .render(null));
    }

    @Test
    public void testHelperValidation() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addTag().build()).build();
        MustacheExceptionAssert.expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> engine.compileMustache("tag_helper_validation01", "{{tag}}"));
    }

}
