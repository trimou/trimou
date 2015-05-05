package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.ExceptionAssert;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class OptionsTest extends AbstractTest {

    @Test
    public void testParametes() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        List<Object> params = options.getParameters();
                        assertEquals(3, params.size());
                        assertEquals("1", params.get(0));
                        assertEquals(10, params.get(1));
                        assertNull(params.get(2));
                    }
                }).build();
        engine.compileMustache("helper_params",
                "{{test \"1\" this.age nonexisting}}").render(new Hammer());
    }

    @Test
    public void testHash() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        final Map<String, Object> hash = options.getHash();
                        assertEquals(3, hash.size());
                        assertEquals("1", hash.get("first"));
                        assertEquals(10, hash.get("second"));
                        assertNull(hash.get("third"));
                        ExceptionAssert.expect(
                                UnsupportedOperationException.class).check(
                                new Runnable() {
                                    public void run() {
                                        hash.remove("first");
                                    }
                                });
                    }
                }).build();
        engine.compileMustache("helper_params",
                "{{test first=\"1\" second=this.age third=nonexisting}}")
                .render(new Hammer());
    }

    @Test
    public void testPush() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        options.push("OK");
                        options.fn();
                    }
                }).build();
        assertEquals(
                "OK|HAMMER",
                engine.compileMustache("helper_params",
                        "{{#test}}{{this}}{{/test}}|{{this}}").render(
                        new Hammer()));
    }

    @Test
    public void testPop() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        options.pop();
                    }
                }).build();

        MustacheExceptionAssert.expect(
                MustacheProblem.RENDER_HELPER_INVALID_POP_OPERATION).check(
                new Runnable() {
                    public void run() {
                        engine.compileMustache("helper_params", "{{test}}")
                                .render(new Hammer());
                    }
                });
    }

    @Test
    public void testPartial() {
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(
                        new MapTemplateLocator(ImmutableMap.of("foo",
                                "{{this}}")))
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        options.partial("foo");
                        ;
                    }
                }).build();
        assertEquals(
                "HELLO",
                engine.compileMustache("helper_partial", "{{test}}").render(
                        "HELLO"));
    }

    @Test
    public void testPeek() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        options.append(options.peek().toString());
                    }
                }).build();
        assertEquals("HELLO", engine.compileMustache("helper_peek", "{{test}}")
                .render("HELLO"));
    }

    @Test
    public void testGetAppendable() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        try {
                            options.getAppendable().append(
                                    options.peek().toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).build();
        assertEquals("HELLO",
                engine.compileMustache("helper_getappendable", "{{test}}")
                        .render("HELLO"));
    }

    @Test
    public void testGetContentLiteralBlock() {
        final StringBuilder builder = new StringBuilder();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        builder.append(options.getContentLiteralBlock());
                    }
                }).build();
        String literal = "This is /n a {{foo bar='ok'}}{{#each this}}{{.}}{{/each}}/n/n {{join 'foo' 'bar' delimiter=' : '}}";
        engine.compileMustache("helper_getcontentliteralblock",
                "{{#test}}" + literal + "{{/test}}").render(null);
        assertEquals(literal, builder.toString());
    }

    @Test
    public void testGetTagInfo() {
        final AtomicReference<MustacheTagInfo> reference = new AtomicReference<MustacheTagInfo>();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        reference.set(options.getTagInfo());
                    }
                }).build();
        engine.compileMustache("helper_gettaginfo",
                "{{#test}}This is {{foo}} and {{^bar}}{{/bar}}{{/test}}")
                .render(null);
        assertEquals(MustacheTagType.SECTION, reference.get().getType());
        assertEquals("test", reference.get().getText());
        List<MustacheTagInfo> children = reference.get().getChildTags();
        assertEquals(2, children.size());
        assertEquals(MustacheTagType.VARIABLE, reference.get().getChildTags()
                .get(0).getType());
        assertEquals(MustacheTagType.INVERTED_SECTION, reference.get()
                .getChildTags().get(1).getType());
    }

    @Test
    public void testFnAppendable() {
        final StringBuilder builder = new StringBuilder();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        options.fn(builder);
                    }
                }).build();
        String literal = "This is /n a foo";
        assertEquals(
                "",
                engine.compileMustache("helper_fnappendable",
                        "{{#test}}" + literal + "{{/test}}").render(null));
        assertEquals(literal, builder.toString());
    }

    @Test
    public void testGetValue() {
        final AtomicBoolean released = new AtomicBoolean(false);
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(new AbstractResolver(1) {
                    @Override
                    public Object resolve(Object contextObject, String name,
                            ResolutionContext context) {
                        context.registerReleaseCallback(new ReleaseCallback() {

                            @Override
                            public void release() {
                                released.set(true);
                            }
                        });
                        return "foo";
                    }
                }).registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Options options) {
                        options.append(options.getValue("key").toString());
                    }
                }).build();
        assertEquals(
                "foo",
                engine.compileMustache("helper_getvalue", "{{test}}").render(
                        "bar"));
        assertTrue(released.get());
    }

}
