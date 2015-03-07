package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.trimou.AssertUtil.assertCompilationFails;

import java.math.BigDecimal;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.exception.MustacheProblem;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Martin Kouba
 */
public class BuiltInHelpersTest extends AbstractEngineTest {

    @Test
    public void testEachHelper() {
        assertEquals(
                "foo,bar",
                engine.compileMustache("each_helper1",
                        "{{#each this}}{{this}}{{#iterHasNext}},{{/iterHasNext}}{{/each}}")
                        .render(new Object[] { "foo", "bar" }));
        assertEquals(
                "foo,bar",
                engine.compileMustache("each_helper2",
                        "{{#each this}}{{this}}{{#iterHasNext}},{{/iterHasNext}}{{/each}}")
                        .render(ImmutableSet.of("foo", "bar")));
        assertEquals(
                "foo,baz",
                engine.compileMustache("each_helper3",
                        "{{#each data apply=myFilter}}{{this}}{{#if iter.hasNext}},{{/if}}{{/each}}")
                        .render(ImmutableMap.<String, Object> of("myFilter",
                                Functions.from(new Function<Object, Object>() {
                                    @Override
                                    public Object apply(Object input) {
                                        return input.toString().equals("bar") ? org.trimou.handlebars.EachHelper.SKIP_RESULT : input;
                                    }
                                }), "data",
                                ImmutableSet.of("foo", "bar", "baz"))));
        assertEquals(
                "",
                engine.compileMustache("each_helper4",
                        "{{#each data apply=myFilter}}{{this}}{{/each}}")
                        .render(ImmutableMap.<String, Object> of("myFilter",
                                new org.trimou.handlebars.Function() {
                                    @Override
                                    public Object apply(Object value) {
                                        return org.trimou.handlebars.EachHelper.SKIP_RESULT;
                                    }
                                }, "data", ImmutableSet.of("foo", "bar", "baz"))));
        assertEquals(
                "332",
                engine.compileMustache("each_helper5",
                        "{{#each data apply=toLength}}{{this}}{{/each}}")
                        .render(ImmutableMap.<String, Object> of("toLength",
                                new org.trimou.handlebars.Function() {
                                    @Override
                                    public Object apply(Object value) {
                                        return value.toString().length();
                                    }
                                }, "data", ImmutableSet.of("foo", "bar", "ba"))));
        assertCompilationFails(engine, "each_helper_fail1",
                "{{#each}}{{this}}{{/each}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "each_helper_fail2", "{{each}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        MustacheExceptionAssert.expect(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS).check(
                new Runnable() {
                    public void run() {
                        engine.compileMustache("each_helper_filter_fail1",
                                "{{#each data apply=foo}}{{/each}}").render(
                                ImmutableMap.<String, Object> of("data",
                                        new Object[] {"foo"}, "foo", Boolean.FALSE));
                    }
                });
    }

    @Test
    public void testIfHelper() {
        assertEquals(
                "hello",
                engine.compileMustache("if_helper1", "{{#if this}}hello{{/if}}")
                        .render(Boolean.TRUE));
        assertEquals(
                "",
                engine.compileMustache("if_helper2", "{{#if this}}hello{{/if}}")
                        .render(false));
        assertEquals(
                "hello",
                engine.compileMustache("if_helper3",
                        "{{#if this this}}hello{{/if}}").render(Boolean.TRUE));
        assertEquals(
                "",
                engine.compileMustache("if_helper4",
                        "{{#if this \"\"}}hello{{/if}}").render(Boolean.TRUE));
        assertEquals(
                "hello",
                engine.compileMustache("if_helper5",
                        "{{#if this \"true\" this}}hello{{/if}}").render(
                        Boolean.TRUE));
        assertEquals(
                "hello",
                engine.compileMustache("if_helper6",
                        "{{#if this \"\" logic=\"or\"}}hello{{/if}}").render(
                        Boolean.TRUE));
        assertCompilationFails(engine, "if_helper_fail1",
                "{{#if}}{{this}}{{/if}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "if_helper_fail2", "{{if}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testUnlessHelper() {
        assertEquals(
                "hello",
                engine.compileMustache("unless_helper1",
                        "{{#unless this}}hello{{/unless}}").render(
                        Boolean.FALSE));
        assertEquals(
                "",
                engine.compileMustache("unless_helper2",
                        "{{#unless this}}hello{{/unless}}").render(true));
        assertEquals(
                "",
                engine.compileMustache("unless_helper3",
                        "{{#unless this this}}hello{{/unless}}").render(true));
        assertEquals(
                "hello",
                engine.compileMustache("unless_helper4",
                        "{{#unless this \"\"}}hello{{/unless}}").render(true));
        assertEquals(
                "",
                engine.compileMustache("unless_helper5",
                        "{{#unless this \"\" logic=\"and\"}}hello{{/unless}}")
                        .render(true));
        assertEquals(
                "",
                engine.compileMustache("unless_helper6",
                        "{{#unless this}}hello{{/unless}}").render(
                        new BigDecimal("0.1")));
        assertEquals(
                "hello",
                engine.compileMustache("unless_helper6",
                        "{{#unless this}}hello{{/unless}}").render(
                        new BigDecimal("0.000")));
        assertCompilationFails(engine, "unless_helper_fail1",
                "{{#unless}}{{this}}{{/unless}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "unless_helper_fail2", "{{unless}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testWithHelper() {
        assertEquals(
                "10",
                engine.compileMustache("with_helper1",
                        "{{#with this}}{{age}}{{/with}}").render(new Hammer()));
        assertCompilationFails(engine, "with_helper_fail1",
                "{{#with}}{{this}}{{/with}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "with_helper_fail2", "{{with}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertEquals(
                "10",
                engine.compileMustache("with_helper_nested",
                        "{{#with this}}{{#with age}}{{intValue}}{{/with}}{{/with}}")
                        .render(new Hammer()));
    }

    @Test
    public void testIsHelper() {
        assertEquals(
                "bye",
                engine.compileMustache("is_helper1",
                        "{{is this \"hello\" \"bye\"}}").render(false));
        assertEquals("",
                engine.compileMustache("is_helper2", "{{is this \"hello\"}}")
                        .render(false));
        assertEquals("hello",
                engine.compileMustache("is_helper3", "{{is this \"hello\"}}")
                        .render(true));
        assertEquals("&lt;html&gt;",
                engine.compileMustache("is_helper4", "{{is this \"<html>\"}}")
                        .render(true));
        assertEquals(
                "<html>",
                engine.compileMustache("is_helper5", "{{{is this \"<html>\"}}}")
                        .render(true));
    }

}
