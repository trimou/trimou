package org.trimou.minify;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class MinifyLambdaTest {

    @Test
    public void testDefaultHtmlLambda() {
        MinifyLambda lambda = Minify.htmlLambda();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addGlobalData("mini", lambda).registerCallback(lambda).build();
        assertEquals(
                "<html><body><!-- My comment -->\n\n<p><strong> Man</strong></p></body></html>",
                engine.compileMustache(
                        "minify_html",
                        "<html><body><!-- My comment -->\n\n<p>{{#mini}}<strong>  Man</strong><!-- To be replaced -->{{/mini}}</p></body></html>")
                        .render(ImmutableMap.<String, Object> of("foo", "FOO")));
    }

    @Test
    public void testDefaultXmlLambda() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addGlobalData("mini", Minify.xmlLambda()).build();
        assertEquals(
                "<foo> <!-- My comment --> <bar>Hey FOO!</bar> </foo>",
                engine.compileMustache(
                        "minify_lambda_xml",
                        "<foo> <!-- My comment --> {{#mini}} <bar>Hey {{foo}}!</bar>\n\n {{/mini}} </foo>")
                        .render(ImmutableMap.<String, Object> of("foo", "FOO")));
    }

}
