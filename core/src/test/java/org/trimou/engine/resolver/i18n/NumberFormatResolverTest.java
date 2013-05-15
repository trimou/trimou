package org.trimou.engine.resolver.i18n;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.LocaleSupport;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class NumberFormatResolverTest extends AbstractTest {

	@Override
	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder()
				.setLocaleSupport(new LocaleSupport() {

					@Override
					public Locale getCurrentLocale() {
						return new Locale("cs", "CZ");
					}
				}).addResolver(new NumberFormatResolver()).build();

	}

	@Test
	public void testInterpolation() {

		String templateContents = "{{number.format}}|{{number.formatPercent}}|{{number.formatCurrency}}";
		Mustache mustache = engine.compileMustache("number", templateContents);

		assertEquals("1,5|150%|1,5 Kč", mustache.render(ImmutableMap
				.<String, Object> of("number", new BigDecimal("1.5"))));
	}

}
