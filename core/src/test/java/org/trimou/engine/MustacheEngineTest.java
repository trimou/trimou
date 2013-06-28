package org.trimou.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.ArchiveType;
import org.trimou.Mustache;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class MustacheEngineTest extends AbstractEngineTest {

	@Before
	public void buildEngine() {
	}

	@Test
	public void testGlobalData() {

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

		String templateContents = "{{foo}}| {{#bold}}Hello{{/bold}} {{#italic}}world{{/italic}}!|{{#archiveType.values}}{{this.suffix}}{{#iterHasNext}}, {{/iterHasNext}}{{/archiveType.values}}|{{archiveTypes.JAR}}";
		Mustache mustache = MustacheEngineBuilder.newBuilder()
				.addGlobalData("foo", true)
				.addGlobalData("archiveType", ArchiveType.class)
				.addGlobalData("bold", bold).addGlobalData("italic", italic)
				.build().compileMustache("global_data", templateContents);

		assertEquals("true| <b>Hello</b> <i>world</i>!|jar, war, ear|JAR",
				mustache.render(null));
	}

	@Test
	public void testDelimitersConfiguration() {
		assertEquals(
				"bar",
				MustacheEngineBuilder
						.newBuilder()
						.setProperty(EngineConfigurationKey.START_DELIMITER,
								"<%")
						.setProperty(EngineConfigurationKey.END_DELIMITER, "//")
						.build()
						.compileMustache("delimiters_configuration", "<%foo//")
						.render(ImmutableMap.<String, Object> of("foo", "bar")));

	}

	@Test
	public void testDebugModeDisablesTemplateCache() {
		MustacheEngine engine = MustacheEngineBuilder
				.newBuilder()
				.setProperty(EngineConfigurationKey.DEBUG_MODE_ENABLED, true)
				.addTemplateLocator(
						new MapTemplateLocator(ImmutableMap.of("foo", "Hey!")))
				.build();
		assertNotEquals(engine.getMustache("foo"), engine.getMustache("foo"));
	}

}
