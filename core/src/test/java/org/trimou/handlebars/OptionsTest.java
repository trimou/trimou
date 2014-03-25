package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.ExceptionAssert;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;
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

}
