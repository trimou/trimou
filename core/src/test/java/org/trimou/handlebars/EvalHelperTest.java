package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.interpolation.BracketDotKeySplitter;
import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.handlebars.EvalHelper.BracketDotNotation;
import org.trimou.util.ImmutableList;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class EvalHelperTest extends AbstractTest {

    // see #117
    @Test
    public void testHelperHandleNulls() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addEval().build())
                .build();

        Map<String, Object> mappings = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("_mappings", mappings);
        assertEquals("", engine.compileMustache("{{eval '_mappings.language' locale}}").render(data));

        Map<String, Object> languages = new HashMap<>();
        mappings.put("language", languages);
        assertEquals("", engine.compileMustache("{{eval '_mappings.language' locale}}").render(data));

        languages.put("de", "deu");
        assertEquals("", engine.compileMustache("{{eval '_mappings.language' locale}}").render(data));

        data.put("locale", "de");
        assertEquals("deu", engine.compileMustache("{{eval '_mappings.language' locale}}").render(data));
    }

    @Test
    public void testHelper() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addEval().build())
                .build();
        String[] array = { "alpha", "bravo", "charlie" };
        List<String> list = ImmutableList.of("foo", "bar", "baz");
        Map<String, Object> data = ImmutableMap
                .of("array", array, "list", list);
        assertEquals(
                "foo:alpha,bar:bravo,baz:charlie",
                engine.compileMustache(
                        "eval_helper1",
                        "{{#each list}}{{this}}:{{eval 'array' iter.position}}{{#if iter.hasNext}},{{/if}}{{/each}}")
                        .render(data));
    }

    @Test
    public void testCustomNotation() {
        final MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .setKeySplitter(new BracketDotKeySplitter())
                .registerHelpers(
                        HelpersBuilder.empty()
                                .addEval(new BracketDotNotation()).build())
                .build();
        assertEquals(
                "val",
                engine.compileMustache("eval_helper_notation",
                        "{{eval 'this' this.keySet.iterator.next}}").render(
                        ImmutableMap.of("my.key", "val")));
    }

    @Test
    public void testNullValue() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setMissingValueHandler(new MissingValueHandler() {

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }

                    @Override
                    public Object handle(MustacheTagInfo tagInfo) {
                        return "Hello";
                    }
                }).registerHelpers(HelpersBuilder.empty().addEval().build())
                .build();
        assertEquals(
                "Hello",
                engine.compileMustache("eval_null_val1", "{{eval 'foo' 'bar'}}")
                        .render(null));
        assertEquals(
                "",
                engine.compileMustache("eval_null_val2",
                        "{{#eval 'foo' 'bar'}}{{this}}{{/eval}}").render(null));
    }

}
