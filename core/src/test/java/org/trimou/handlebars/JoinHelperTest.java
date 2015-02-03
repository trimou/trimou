package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;
import org.trimou.lambda.InputLiteralLambda;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class JoinHelperTest extends AbstractTest {

    @Test
    public void testJoinHelper() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addJoin().build())
                .addGlobalData("li", new InputLiteralLambda() {

                    @Override
                    public boolean isReturnValueInterpolated() {
                        return false;
                    }

                    @Override
                    public String invoke(String text) {
                        return "<li>" + text + "</li>";
                    }
                }).build();

        String[] array = { "alpha", "bravo", "charlie" };
        List<String> list = ImmutableList.of("foo", "bar", "baz");

        assertEquals(
                "alphabravocharliefoo",
                engine.compileMustache("join_helper1",
                        "{{join this 'foo' nullValue}}").render(array));
        assertEquals(
                "alpha : bravo : charlie",
                engine.compileMustache("join_helper2",
                        "{{join this delimiter=' : '}}").render(array));

        assertEquals(
                "foo,bar,baz,alpha,bravo,charlie",
                engine.compileMustache("join_helper3",
                        "{{join list array delimiter=','}}").render(
                        ImmutableMap.of("array", array, "list", list)));

        assertEquals(
                "start,foo,bar,baz,middle,alpha,bravo,charlie,end",
                engine.compileMustache("join_helper4",
                        "{{join 'start' list 'middle' array 'end' delimiter=','}}")
                        .render(ImmutableMap.of("array", array, "list", list)));

        assertEquals(
                "<start><end>",
                engine.compileMustache("join_helper5",
                        "{{&join '<start>' '<end>'}}").render(
                        ImmutableMap.of("array", array, "list", list)));

        assertEquals(
                "&lt;start&gt;&lt;end&gt;",
                engine.compileMustache("join_helper6",
                        "{{join '<start>' '<end>'}}").render(
                        ImmutableMap.of("array", array, "list", list)));

        assertEquals(
                "<li>foo</li>\n<li>bar</li>\n<li>baz</li>\n<li>Me</li>\n<li>alpha</li>\n<li>bravo</li>\n<li>charlie</li>",
                engine.compileMustache("join_helper7",
                        "{{&join list 'Me' array delimiter='\n' lambda=li}}")
                        .render(ImmutableMap.of("array", array, "list", list)));
    }

    @Test
    public void testJoinHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addJoin().build())
                .build();

        MustacheExceptionAssert.expect(
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE).check(
                new Runnable() {
                    public void run() {
                        engine.compileMustache("join_helper_validation01",
                                "{{join}}");
                    }
                });
        MustacheExceptionAssert.expect(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS).check(
                new Runnable() {
                    public void run() {
                        engine.compileMustache("join_helper_validation02",
                                "{{join 'Me' lambda='foo'}}").render(null);
                    }
                });
    }

}
