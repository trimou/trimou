package org.trimou.handlebars.i18n;

import static org.junit.Assert.assertEquals;

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
public class ResourceBundleHelperTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
        engine = MustacheEngineBuilder.newBuilder()
                .setLocaleSupport(new LocaleSupport() {
                    @Override
                    public Locale getCurrentLocale() {
                        return new Locale("en");
                    }

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }
                }).registerHelper("messages", new ResourceBundleHelper("messages")).build();

    }

    @Test
    public void testInterpolation() {
        String templateContents = "{{messages \"echo_one\"}},{{messages \"echo.two\"}},{{messages key}}";
        Mustache mustache = engine.compileMustache("bundle", templateContents);
        assertEquals("Hello,Hey,echo", mustache.render(ImmutableMap.of("key", "echo")));
    }

    @Test
    public void testBaseName() {
        String templateContents = "{{messages \"test.key.bravo\" baseName=\"trimou\"}}";
        Mustache mustache = engine.compileMustache("bundle", templateContents);
        assertEquals("42", mustache.render(null));
    }

}
