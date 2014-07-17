package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;
import org.trimou.Mustache;
import org.trimou.engine.parser.Template;
import org.trimou.lambda.InputProcessingLambda;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class SectionSegmentTest extends AbstractEngineTest {

    @Test
    public void testBoolean() {

        String templateContents = "{{#Boolean}}-{{/Boolean}}{{#boolean}}-{{/boolean}}!";
        Mustache mustache = engine.compileMustache("boolean", templateContents);

        assertEquals("--!", mustache.render(ImmutableMap.<String, Object> of(
                "boolean", true, "Boolean", Boolean.TRUE, "true", "true")));
        assertEquals("!", mustache.render(ImmutableMap.<String, Object> of(
                "boolean", false)));
    }

    @Test
    public void testIterable() {

        Mustache mustache = engine
                .compileMustache("iterable", "{{#numbers}}la{{iterIndex}}{{iterHasNext}}|{{/numbers}}");

        assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", Collections.emptyList())));
        assertEquals("la1true|la2true|la3false|", mustache.render(ImmutableMap
                .<String, Object> of("numbers", ImmutableList.of(1, 2, 3))));

        mustache = engine
                .compileMustache("iterable2", "{{#numbers}}la{{iter.index}}{{iter.hasNext}}|{{/numbers}}");

        assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", Collections.emptyList())));
        assertEquals("la1true|la2true|la3false|", mustache.render(ImmutableMap
                .<String, Object> of("numbers", ImmutableList.of(1, 2, 3))));

    }

    @Test
    public void testArray() {

        Mustache mustache = engine.compileMustache("array", "{{#numbers}}la{{iterIndex}}{{iterHasNext}}|{{/numbers}}");

        assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", new Integer[] {})));
        assertEquals("la1true|la2true|la3false|", mustache.render(ImmutableMap
                .<String, Object> of("numbers", new Integer[] { 1, 2, 3 })));

        mustache = engine.compileMustache("array2", "{{#numbers}}la{{iter.index}}{{iter.hasNext}}|{{/numbers}}");

        assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", new Integer[] {})));
        assertEquals("la1true|la2true|la3false|", mustache.render(ImmutableMap
                .<String, Object> of("numbers", new Integer[] { 1, 2, 3 })));

    }

    @Test
    public void testLambdas() {

        Lambda literal = new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return "prefix_" + text;
            }

            @Override
            public boolean isReturnValueInterpolated() {
                return false;
            }
        };

        Lambda processed = new InputProcessingLambda() {

            @Override
            public String invoke(String text) {
                return "prefix_" + text;
            }

            @Override
            public boolean isReturnValueInterpolated() {
                return false;
            }
        };

        String templateContents = "{{#lambda}}{{foo}}{{/lambda}}";
        Mustache mustache = engine.compileMustache("lambda", templateContents);

        assertEquals("prefix_{{foo}}", mustache.render(ImmutableMap
                .<String, Object> of("foo", "Mine", "lambda", literal)));
        assertEquals("prefix_Mine", mustache.render(ImmutableMap
                .<String, Object> of("foo", "Mine", "lambda", processed)));
    }

    @Test
    public void testNestedContext() {

        String templateContents = "Hello {{#test}}{{name}}{{/test}}!";
        Mustache mustache = engine.compileMustache("nested", templateContents);

        assertEquals("Hello Edgar!", mustache.render(ImmutableMap
                .<String, Object> of("test", new Hammer())));
        assertEquals("Hello !",
                mustache.render(Collections.singletonMap("test", null)));
    }

    @Test
    public void testFirstAndLast() {

        String templateContents = "{{#this}}{{#iterIsFirst}}{{this}}|{{/iterIsFirst}}{{#iterIsLast}}{{this}}|{{/iterIsLast}}{{/this}}";
        Mustache mustache = engine.compileMustache("iter_first_last",
                templateContents);

        assertEquals("1|3|", mustache.render(new String[] { "1", "2", "3" }));
    }

    @Test
    public void testSegmentSize() {
        Template template = (Template) engine.compileMustache("foo",
                "{{foo}}bar\nbaz{{#qux}}lala{{/qux}}");
        assertEquals(6, template.getRootSegment().getSegmentsSize(true));
    }

}
