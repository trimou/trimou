package org.trimou.engine.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ValidationTest extends AbstractEngineTest {

    @Test
    public void testTagContentValidation() {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED,
                        false).build();

        testInvalidTemplate(engine, "{{fo{{o}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "tag_contains_start_delimiter");
        testInvalidTemplate(engine, "{{foo}} {{ boo {{bar}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "not_a_nonwhitespace_character_sequence");
        testInvalidTemplate(engine, "{{foo and me}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "not_a_nonwhitespace_character_sequence");
        testInvalidTemplate(engine, "{{#fo\no}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "not_a_nonwhitespace_character_sequence");
        testInvalidTemplate(engine, "{{>fo .txt}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "not_a_nonwhitespace_character_sequence");
        assertEquals(
                "",
                engine.compileMustache("engine", "{{! Hello there my friends}}")
                        .render(null));
        testInvalidTemplate(engine, "Hello\n\n\n{{}}",
                MustacheProblem.COMPILE_INVALID_TAG, "invalid_tag01");

        engine = MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED,
                        true).build();

        testInvalidTemplate(engine, "{{foo}} {{ boo {{bar}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "not_a_nonwhitespace_character_sequence");
        testValidTemplate(engine, "{{foo and me}}");
        testInvalidTemplate(engine, "{{#fo\no}}",
                MustacheProblem.COMPILE_INVALID_TEMPLATE,
                "not_a_nonwhitespace_character_sequence");
        testInvalidTemplate(engine, "{{>fo .txt}}",
                MustacheProblem.COMPILE_INVALID_TAG,
                "not_a_nonwhitespace_character_sequence");
        assertEquals(
                "",
                engine.compileMustache("engine", "{{! Hello there my friends}}")
                        .render(null));
        testInvalidTemplate(engine, "Hello\n\n\n{{}}",
                MustacheProblem.COMPILE_INVALID_TAG, "invalid_tag01");
    }

    @Test
    public void testInvalidDelimiters() {
        testInvalidTemplate(engine, "{{==}}",
                MustacheProblem.COMPILE_INVALID_DELIMITERS,
                "invalid_delimiters01");
        testInvalidTemplate(engine, "{{=%% %%}}",
                MustacheProblem.COMPILE_INVALID_DELIMITERS,
                "invalid_delimiters02");
        testInvalidTemplate(engine, "{{= %}}",
                MustacheProblem.COMPILE_INVALID_DELIMITERS,
                "invalid_delimiters03");
        testInvalidTemplate(engine, "{{= =}}",
                MustacheProblem.COMPILE_INVALID_DELIMITERS,
                "invalid_delimiters04");
    }

    @Test
    public void testInvalidSectionEnds() {
        testInvalidTemplate(engine, "Hello {{#foo}} and...",
                MustacheProblem.COMPILE_INVALID_TEMPLATE, "no_section_end");
        testInvalidTemplate(engine, "Hello {{#foo}} and... {{/foo}}{{/bar}}",
                MustacheProblem.COMPILE_INVALID_SECTION_END,
                "invalid_section_end01");
        testInvalidTemplate(engine, "Hello {{/foo}}",
                MustacheProblem.COMPILE_INVALID_SECTION_END,
                "invalid_section_end02");
        testInvalidTemplate(engine, "Hello,\n{{^foo}}{{/bar}}",
                MustacheProblem.COMPILE_INVALID_SECTION_END,
                "invalid_section_end03");
    }

    @Test
    public void testIncompleteTag() {
        testInvalidTemplate(engine, "{{ foo",
                MustacheProblem.COMPILE_INVALID_TEMPLATE, "incomplete_tag");
    }

    private void testInvalidTemplate(MustacheEngine engine, String template,
            MustacheProblem expectedProblem, String description) {
        try {
            engine.compileMustache("validation_" + description, template);
            fail("Problem expected: " + expectedProblem);
        } catch (MustacheException e) {
            if (!expectedProblem.equals(e.getCode())) {
                fail("Invalid problem: " + e.getMessage());
            }
            System.out.println(e.getMessage());
        } catch (Exception e) {
            fail("Incorrect exception: " + e);
        }
    }

    private void testValidTemplate(MustacheEngine engine, String template) {
        engine.compileMustache("validation", template);
    }

}
