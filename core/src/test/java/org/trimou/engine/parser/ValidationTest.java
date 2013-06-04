package org.trimou.engine.parser;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ValidationTest extends AbstractTest {

	@Test
	public void testTemplateValidation() {

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
		testInvalidTemplate(engine, "{{==}}",
				MustacheProblem.COMPILE_INVALID_DELIMITERS,
				"invalid_delimiters01");
		testInvalidTemplate(engine, "{{=%% %%}}",
				MustacheProblem.COMPILE_INVALID_DELIMITERS,
				"invalid_delimiters01");
		testInvalidTemplate(engine, "{{= %}}",
				MustacheProblem.COMPILE_INVALID_DELIMITERS,
				"invalid_delimiters02");
		testInvalidTemplate(engine, "{{= =}}",
				MustacheProblem.COMPILE_INVALID_DELIMITERS,
				"invalid_delimiters03");
		testInvalidTemplate(engine, "Hello\n\n\n{{}}",
				MustacheProblem.COMPILE_INVALID_TAG,
				"invalid_tag01");

		testInvalidTemplate(engine, "{{foo",
				MustacheProblem.COMPILE_INVALID_TEMPLATE,
				"incomplete_tag");
	}

	private void testInvalidTemplate(MustacheEngine factory, String template,
			MustacheProblem expectedProblem, String description) {
		try {
			factory.compileMustache("validation_" + description, template);
			fail();
		} catch (MustacheException e) {
			if (!expectedProblem.equals(e.getCode())) {
				fail("Invalid problem");
			}
			System.out.println(e.getMessage());
		} catch (Exception e) {
			fail("Incorrect exception: " + e);
		}
	}

}
