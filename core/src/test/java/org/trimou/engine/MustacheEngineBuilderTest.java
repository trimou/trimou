package org.trimou.engine;

import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngineBuilder.EngineBuiltCallback;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.listener.AbstractMustacheListener;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.locator.FileSystemTemplateLocator;
import org.trimou.engine.resolver.i18n.DateTimeFormatResolver;
import org.trimou.engine.text.TextSupport;

/**
 *
 * @author Martin Kouba
 */
public class MustacheEngineBuilderTest extends AbstractTest {

    @Test
    public void testBuilderIsImmutable() {

        MustacheEngineBuilder builder = MustacheEngine.builder();
        builder.build();
        try {
            builder.addGlobalData("foo", 10);
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.addMustacheListener(new AbstractMustacheListener() {
            });
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.addResolver(new DateTimeFormatResolver());
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.addTemplateLocator(new FileSystemTemplateLocator(11, "foo"));
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.setLocaleSupport(new LocaleSupport() {

                @Override
                public void init(Configuration configuration) {
                }

                @Override
                public Set<ConfigurationKey> getConfigurationKeys() {
                    return null;
                }

                @Override
                public Locale getCurrentLocale() {
                    return null;
                }
            });
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.setTextSupport(new TextSupport() {

                @Override
                public void init(Configuration configuration) {
                }

                @Override
                public Set<ConfigurationKey> getConfigurationKeys() {
                    return null;
                }

                @Override
                public String escapeHtml(String input) {
                    return null;
                }
            });
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.omitServiceLoaderConfigurationExtensions();
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.setProperty("foo", "bar");
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.setProperty(
                    EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK, "bar");
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            builder.registerCallback(new EngineBuiltCallback() {
                @Override
                public void engineBuilt(MustacheEngine engine) {
                }
            });
            fail();
        } catch (Exception e) {
            // Expected
        }

    }

}
