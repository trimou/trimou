package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.ArchiveType;
import org.trimou.Hammer;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.interpolation.NoOpMissingValueHandler;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableList;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class InvokeHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addInvoke().build())
                .build();
        List<String> list = ImmutableList.of("foo", "bar", "baz");
        Map<String, Object> data = ImmutableMap.of("string", "foo", "list",
                list);
        assertEquals("oo", engine
                .compileMustache("invoke_01", "{{invoke 1 m='substring'}}")
                .render("foo"));
        assertEquals("bar",
                engine.compileMustache("invoke_02", "{{invoke 1 m='get'}}")
                        .render(list));
        assertEquals("FOOBARBAZ",
                engine.compileMustache("invoke_03",
                        "{{#invoke 'list' m='get'}}{{#each this}}{{toUpperCase}}{{/each}}{{/invoke}}")
                        .render(data));
        assertEquals("3", engine
                .compileMustache("invoke_04", "{{invoke on=list m='size'}}")
                .render(data));
        assertEquals(
                "false", engine
                        .compileMustache("invoke_05",
                                "{{invoke on=list method='isEmpty'}}")
                        .render(data));
        assertEquals("boo",
                engine.compileMustache("invoke_06",
                        "{{invoke 'f' 'b' on='foo' m='replace'}}")
                        .render(null));
        assertEquals("2",
                engine.compileMustache("invoke_07",
                        "{{#invoke ':' on='foo:bar' m='split'}}{{this.length}}{{/invoke}}")
                        .render(null));
    }

    @Test
    public void testStaticMethod() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addInvoke().build())
                .build();
        List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        assertEquals("bazbarfoo",
                engine.compileMustache("invoke_static_01",
                        "{{invoke this class='java.util.Collections' m='reverse'}}{{#each this}}{{this}}{{/each}}")
                        .render(list));
        assertEquals("war",
                engine.compileMustache("invoke_static_02",
                        "{{#invoke 'WAR' class='org.trimou.ArchiveType' m='valueOf'}}{{suffix}}{{/invoke}}")
                        .render(null));
        assertEquals("1",
                engine.compileMustache("invoke_static_03",
                        "{{#invoke 'MILLISECONDS' class='java.util.concurrent.TimeUnit' m='valueOf'}}{{invoke 1000l m='toSeconds'}}{{/invoke}}")
                        .render(null));
        assertEquals("war",
                engine.compileMustache("invoke_static_04",
                        "{{#invoke 'WAR' class=this m='valueOf'}}{{suffix}}{{/invoke}}")
                        .render(ArchiveType.class));
    }

    @Test
    public void testValidation() {
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addInvoke().build())
                        .build().compileMustache("invoke_validation_01",
                                "{{invoke 'foo' on=this}}"));
    }

    @Test
    public void testMissingValueHandler() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setMissingValueHandler(new NoOpMissingValueHandler() {

                    @Override
                    public Object handle(MustacheTagInfo tagInfo) {
                        return "foo";
                    }
                }).registerHelpers(HelpersBuilder.empty().addInvoke().build())
                .build();
        assertEquals("foo", engine
                .compileMustache("invoke_missing_01", "{{invoke m='getNull'}}")
                .render(new Hammer()));
    }

    @Test
    public void testRenderingErrors() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addInvoke().build())
                .build();
        MustacheExceptionAssert
                .expect(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS)
                .check(() -> engine.compileMustache("invoke_method_not_found",
                        "{{invoke m='foo'}}").render("bar"));
        MustacheExceptionAssert
                .expect(MustacheProblem.RENDER_HELPER_INVALID_OPTIONS)
                .check(() -> engine
                        .compileMustache("invoke_method_ambiguous",
                                "{{invoke 'name' 5L class='java.lang.Long' m='getLong'}}")
                        .render("bar"));
    }

}
