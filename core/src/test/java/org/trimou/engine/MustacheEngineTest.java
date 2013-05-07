package org.trimou.engine;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.api.Lambda;
import org.trimou.api.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.lambda.SpecCompliantLambda;

/**
 *
 * @author Martin Kouba
 */
public class MustacheEngineTest extends AbstractTest {

	@Before
	public void buildEngine() {
	}

	@Test
	public void testGlobalLambdas() {

		Lambda bold = new SpecCompliantLambda() {

			@Override
			public String invoke(String text) {
				return "<b>" + text + "</b>";
			}

			@Override
			public boolean isReturnValueInterpolated() {
				return false;
			}

		};

		Lambda italic = new SpecCompliantLambda() {

			@Override
			public String invoke(String text) {
				return "<i>" + text + "</i>";
			}

			@Override
			public boolean isReturnValueInterpolated() {
				return false;
			}
		};

		String templateContents = "{{#bold}}Hello{{/bold}} {{#italic}}world{{/italic}}!";
		Mustache mustache = MustacheEngineBuilder.newBuilder()
				.addGlobalLambda(bold, "bold")
				.addGlobalLambda(italic, "italic").build()
				.compileMustache("global_lambda", templateContents);

		StringWriter writer = new StringWriter();
		mustache.render(writer, null);
		assertEquals("<b>Hello</b> <i>world</i>!", writer.toString());
	}

}
