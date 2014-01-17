package org.trimou.handlebars.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

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
                }).registerHelper("msg", new ResourceBundleHelper("messages"))
                .build();

    }

    @Test
    public void testInterpolation() {
        String templateContents = "{{msg \"echo_one\"}},{{msg \"echo.two\"}},{{msg key}}";
        Mustache mustache = engine.compileMustache("bundle_helper",
                templateContents);
        assertEquals("Hello,Hey,echo",
                mustache.render(ImmutableMap.of("key", "echo")));
    }

    @Test
    public void testBaseName() {
        String templateContents = "{{msg \"test.key.bravo\" baseName=\"trimou\"}}";
        Mustache mustache = engine.compileMustache("bundle_helper_basename",
                templateContents);
        assertEquals("42", mustache.render(null));
    }

    @Test
    public void testFormat() {
        String defaultFormat = "{{msg \"echo.printf\" \"Martin\"}}";
        String none = "{{msg \"echo.printf\" format=\"none\"}}";
        String printf = "{{msg \"echo.printf\" \"Martin\" format=\"printf\"}}";
        String message = "{{msg \"echo.messageformat\" \"Martin\" format=\"message\"}}";
        String unsupported = "{{msg \"echo.printf\" \"Martin\" format=\"berserk\"}}";
        String wrongArguments = "{{msg \"echo.printf\"}}";

        assertEquals(
                "Hello Martin!",
                engine.compileMustache("bundle_helper_defaultFormat",
                        defaultFormat).render(null));
        assertEquals("Hello %s!",
                engine.compileMustache("bundle_helper_defaultFormat", none)
                        .render(null));
        assertEquals(
                "Hello Martin!",
                engine.compileMustache("bundle_helper_printf", printf).render(
                        null));
        assertEquals("Hello Martin!",
                engine.compileMustache("bundle_helper_message", message)
                        .render(null));
        assertEquals(
                "Hello Martin!",
                engine.compileMustache("bundle_helper_unsupported", unsupported)
                        .render(null));
        try {
            engine.compileMustache("bundle_helper_wrongArguments",
                    wrongArguments).render(null);
            fail();
        } catch (MustacheException e) {
            if (!e.getCode().equals(MustacheProblem.RENDER_IO_ERROR)) {
                fail();
            }
        }
    }

}
