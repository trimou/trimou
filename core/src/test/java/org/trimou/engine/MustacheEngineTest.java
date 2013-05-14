package org.trimou.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.lambda.Lambda;
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
	public void testGlobalValues() {

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

		String templateContents = "{{foo}}| {{#bold}}Hello{{/bold}} {{#italic}}world{{/italic}}!";
		Mustache mustache = MustacheEngineBuilder.newBuilder()
				.addGlobalValue("foo", true)
				.addGlobalValue("bold", bold)
				.addGlobalValue("italic", italic).build()
				.compileMustache("global_value", templateContents);

		assertEquals("true| <b>Hello</b> <i>world</i>!", mustache.render(null));
	}

}
