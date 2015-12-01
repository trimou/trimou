package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.trimou.AssertUtil.assertCompilationFails;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.DummyHelper;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

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
                        .render(ImmutableMap.<String, Object> of(
                                "myFilter",
                                Functions
                                        .from(new com.google.common.base.Function<Object, Object>() {
                                            @Override
                                            public Object apply(Object input) {
                                                return input.toString().equals(
                                                        "bar") ? EachHelper.SKIP_RESULT
                                                        : input;
                                            }
                                        }), "data", ImmutableSet.of("foo",
                                        "bar", "baz"))));
        assertEquals(
                "",
                engine.compileMustache("each_helper4",
                        "{{#each data apply=myFilter}}{{this}}{{/each}}")
                        .render(ImmutableMap.<String, Object> of("myFilter",
                                new Function() {
                                    @Override
                                    public Object apply(Object value) {
                                        return EachHelper.SKIP_RESULT;
                                    }
                                }, "data", ImmutableSet.of("foo", "bar", "baz"))));
        assertEquals(
                "332",
                engine.compileMustache("each_helper5",
                        "{{#each data apply=toLength}}{{this}}{{/each}}")
                        .render(ImmutableMap.<String, Object> of("toLength",
                                new Function() {
                                    @Override
                                    public Object apply(Object value) {
                                        return value.toString().length();
                                    }
                                }, "data", ImmutableSet.of("foo", "bar", "ba"))));
        assertEquals(
                "332",
                engine.compileMustache("each_helper6",
                        "{{#each this as='item'}}{{item.length}}{{/each}}")
                        .render(ImmutableSet.of("foo", "bar", "ba")));
        assertEquals(
                "",
                engine.compileMustache("each_helper7",
                        "{{#each this}}foo{{/each}}").render(null));
        assertEquals(
                "",
                engine.compileMustache("each_helper_empty_array",
                        "{{#each this}}foo{{/each}}").render(new String[]{}));
        assertEquals(
                "",
                engine.compileMustache("each_helper_empty_collection",
                        "{{#each this}}foo{{/each}}").render(new ArrayList<>()));
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
                                        new Object[] { "foo" }, "foo",
                                        Boolean.FALSE));
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
        assertEquals(
                "false",
                engine.compileMustache("if_helper7",
                        "{{#if this \"\" logic=\"and\" else='false'}}hello{{/if}}")
                        .render(Boolean.TRUE));
        assertEquals(
                "Hello me! false",
                engine.compileMustache("if_helper8",
                        "{{#if this.0 else='{this.1} me! {this.0}'}}hello{{/if}}")
                        .render(new Object[] { Boolean.FALSE, "Hello"}));
        assertCompilationFails(engine, "if_helper_fail1",
                "{{#if}}{{this}}{{/if}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "if_helper_fail2", "{{if}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testIfHelperCustomElseDelimiters() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty().addIf("$$", "$$").build(), true)
                .build();
        assertEquals("Length: 5",
                engine.compileMustache("if_helper_custom_else",
                        "{{#if this else='Length: $$this.toString.length$$'}}hello{{/if}}")
                .render(Boolean.FALSE));
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
        assertEquals(
                "7",
                engine.compileMustache("unless_helper7",
                        "{{#unless this else='{this.length}'}}hello{{/unless}}")
                        .render("matched"));
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

    @Test
    public void testOverwriteHelper() {
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelper(HelpersBuilder.EACH, new DummyHelper(), true)
                .registerHelpers(
                        HelpersBuilder.empty()
                                .add(HelpersBuilder.IF, new DummyHelper())
                                .build(), true).build();
        assertEquals(
                "",
                engine.compileMustache("helper_dummy1",
                        "{{#each this}}EACH{{/each}}{{#if this}}IF{{/if}}")
                        .render(new String[] { "hey" }));
    }

}
