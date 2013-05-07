package org.trimou.engine.parser;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheException;
import org.trimou.MustacheProblem;
import org.trimou.api.engine.MustacheEngine;

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
		testInvalidTemplate(engine, "Hello {{^foo}}{{/bar}}",
				MustacheProblem.COMPILE_INVALID_SECTION_END,
				"invalid_section_end03");
		testInvalidTemplate(engine, "Hello {{}}",
				MustacheProblem.COMPILE_INVALID_TAG,
				"invalid_section_end02");

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
		} catch (Exception e) {
			fail("Incorrect exception: " + e);
		}
	}

}
