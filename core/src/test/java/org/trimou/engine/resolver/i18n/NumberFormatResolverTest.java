package org.trimou.engine.resolver.i18n;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.api.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.i18n.NumberFormatResolver;
import org.trimou.spi.engine.LocaleSupport;

import com.google.common.collect.ImmutableMap;

/**
 * TODO more tests
 */
public class NumberFormatResolverTest extends AbstractTest {

	@Override
	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder()
				.setLocaleSupport(new LocaleSupport() {

					@Override
					public Locale getCurrentLocale() {
						return new Locale("cs");
					}
				}).addResolver(new NumberFormatResolver()).build();

	}

	@Test
	public void testInterpolation() {

		String templateContents = "{{number.format}}|{{number.formatPercent}}";
		Mustache mustache = engine.compile("number", templateContents);

		assertEquals("1,5|150%", mustache.render(ImmutableMap.<String, Object> of(
				"number", new BigDecimal("1.5"))));
	}

}
