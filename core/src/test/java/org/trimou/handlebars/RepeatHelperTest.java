package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class RepeatHelperTest extends AbstractTest {

    @Test
    public void testHelper() throws InterruptedException {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty().addRepeat().addInvoke().build())
                .build();
        assertEquals(
                "mememe", engine
                        .compileMustache("repeat_01",
                                "{{#repeat times=3}}me{{/repeat}}")
                        .render(null));
        assertEquals(
                "", engine
                        .compileMustache("repeat_02",
                                "{{#repeat while=this}}me{{/repeat}}")
                        .render(false));
        MustacheExceptionAssert.expect(MustacheProblem.RENDER_GENERIC_ERROR)
                .check(() -> engine
                        .compileMustache("repeat_03",
                                "{{#repeat while='this' limit=3}}me{{/repeat}}")
                        .render(true));
        assertEquals("foobar",
                engine.compileMustache("repeat_04",
                        "{{#with this.iterator}}{{#repeat while=hasNext}}{{next}}{{/repeat}}{{/with}}")
                        .render(ImmutableList.of("foo", "bar")));
        assertEquals("barfoo",
                engine.compileMustache("repeat_05",
                        "{{#invoke this.size on=this m='listIterator'}}{{#repeat while=hasPrevious}}{{previous}}{{/repeat}}{{/invoke}}")
                        .render(ImmutableList.of("foo", "bar")));
    }

    @Test
    public void testValidation() {
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addRepeat().build())
                        .build()
                        .compileMustache("repeat_validation_01", "{{repeat}}"))
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addRepeat().build())
                        .build().compileMustache("repeat_validation_02",
                                "{{#repeat foo='bar'}}{{/repeat}}"))
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addRepeat().build())
                        .build().compileMustache("repeat_validation_03",
                                "{{#repeat times='a'}}{{/repeat}}"));
    }

    @Test
    public void testStreamIteration() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addRepeat().build()).build();
        List<String> data = ImmutableList.of("foo", "baz", "foos");
        Mustache mustache = engine.compileMustache("stream",
                "{{#with this.sequential.iterator}}{{#repeat while=hasNext}}{{next}}/{{/repeat}}{{/with}}");
        assertEquals("foo/baz/", mustache.render(data.stream().filter((e) -> e.toString().length() == 3)));
    }

}
