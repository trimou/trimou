package org.trimou.engine.resolver.i18n;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class NumberFormatResolverTest extends AbstractEngineTest {

	@Override
	@Before
	public void buildEngine() {
		engine = MustacheEngineBuilder.newBuilder()
				.setLocaleSupport(new LocaleSupport() {
					@Override
					public Locale getCurrentLocale() {
						return new Locale("cs", "CZ");
					}
					@Override
					public void init(Configuration configuration) {
					}
					@Override
					public Set<ConfigurationKey> getConfigurationKeys() {
						return Collections.emptySet();
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
