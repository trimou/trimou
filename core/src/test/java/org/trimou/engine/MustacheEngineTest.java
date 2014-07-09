package org.trimou.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.ArchiveType;
import org.trimou.ExceptionAssert;
import org.trimou.Mustache;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.AbstractTemplateLocator;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.exception.MustacheException;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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

        String templateContents = "{{foo}}| {{#bold}}Hello{{/bold}} {{#italic}}world{{/italic}}!|{{#archiveType.values}}{{this.suffix}}{{#iterHasNext}}, {{/iterHasNext}}{{/archiveType.values}}|{{archiveType.JAR}}";
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
                .setProperty(EngineConfigurationKey.DEBUG_MODE, true)
                .addTemplateLocator(
                        new MapTemplateLocator(ImmutableMap.of("foo", "Hey!")))
                .build();
        assertNotEquals(engine.getMustache("foo"), engine.getMustache("foo"));
    }

    @Test
    public void testTemplateCacheExpirationTimeout()
            throws InterruptedException {

        Map<String, String> templates = new HashMap<String, String>();
        templates.put("foo", "0");
        long timeout = 2;

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT,
                        timeout)
                .addTemplateLocator(new MapTemplateLocator(templates)).build();
        assertEquals("0", engine.getMustache("foo").render(null));
        templates.put("foo", "1");
        assertEquals("0", engine.getMustache("foo").render(null));
        Thread.sleep((2 * timeout) * 1000);
        assertEquals("1", engine.getMustache("foo").render(null));
    }

    @Test
    public void testTemplateCacheDisabled() {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED,
                        false)
                .addTemplateLocator(new AbstractTemplateLocator(10) {

                    @Override
                    public Reader locate(String templateId) {
                        return new StringReader(UUID.randomUUID().toString());
                    }

                    @Override
                    public Set<String> getAllIdentifiers() {
                        return null;
                    }
                }).build();

        int size = 10;
        Set<String> values = new HashSet<String>(size);
        for (int i = 0; i < size; i++) {
            values.add(engine.getMustache("foo").render(null));
        }
        assertEquals(size, values.size());
    }

    @Test
    public void testPrecompileAllAvailableTemplates() {

        final List<String> sequence = new ArrayList<String>();

        TemplateLocator locator01 = new AbstractTemplateLocator(10) {

            @Override
            public Reader locate(String templateId) {
                sequence.add("fooLocate");
                return templateId.equals("foo") ? new StringReader("{{foo}}")
                        : null;
            }

            @Override
            public Set<String> getAllIdentifiers() {
                sequence.add("fooGetAllIdentifiers");
                return ImmutableSet.of("foo");
            }
        };

        MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES,
                        true).addTemplateLocator(locator01).build();

        assertEquals(2, sequence.size());
        assertEquals("fooGetAllIdentifiers", sequence.get(0));
        assertEquals("fooLocate", sequence.get(1));
    }

    @Test
    public void testTemplateLocatorReaderIsAlwaysClosed() {

        final String template = "FOO";
        final String illegalTemplate = "{{foo";
        final AtomicBoolean isCloseInvoked = new AtomicBoolean(false);

        TemplateLocator locator = new AbstractTemplateLocator(1) {
            @SuppressWarnings("resource")
            @Override
            public Reader locate(String templateId) {
                return "foo".equals(templateId) ? new MyStringReader(template,
                        isCloseInvoked) : new MyStringReader(illegalTemplate,
                        isCloseInvoked);
            }

            @Override
            public Set<String> getAllIdentifiers() {
                return null;
            }
        };

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();

        assertFalse(isCloseInvoked.get());
        assertEquals(template, engine.getMustache("foo").render(null));
        assertTrue(isCloseInvoked.get());

        isCloseInvoked.set(false);
        assertFalse(isCloseInvoked.get());

        ExceptionAssert.expect(MustacheException.class).check(new Runnable() {
            public void run() {
                engine.getMustache("whatever").render(null);
            }
        });
        assertTrue(isCloseInvoked.get());
    }

    @Test
    public void testHelloWorld() {
        String data = "Hello world!";
        assertEquals(data, MustacheEngineBuilder.newBuilder().build()
                .compileMustache("myTemplateName", "{{this}}").render(data));
    }

    private static class MyStringReader extends StringReader {

        final AtomicBoolean isCloseInvoked;

        public MyStringReader(String s, AtomicBoolean isClosed) {
            super(s);
            this.isCloseInvoked = isClosed;
        }

        @Override
        public void close() {
            isCloseInvoked.set(true);
            super.close();
        }
    }
}
