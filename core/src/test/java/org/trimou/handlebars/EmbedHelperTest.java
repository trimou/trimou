package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.handlebars.EmbedHelper.SourceProcessor;
import org.trimou.util.ImmutableMap;

/**
 * @author Minkyu Cho
 */
public class EmbedHelperTest extends AbstractTest {

    @Test
    public void testEmbeddedHelper() {
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(HelpersBuilder.empty().addEmbed().build())
                .addTemplateLocator(
                        new MapTemplateLocator(ImmutableMap.of("template",
                                "Hello!"))).build();
        assertEquals(
                "<script id=\"template\" type=\"text/template\">\nHello!\n</script>",
                engine.compileMustache("embed_helper01", "{{embed this}}")
                        .render("template"));
        assertEquals(
                "<script id=\"template\" type=\"text/template\">\nHello!\n</script>",
                engine.compileMustache("embed_helper01", "{{embed 'temp' this}}")
                        .render("late"));
    }

    @Test
    public void testCustomSourceProcessor() {
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty().addEmbed(new SourceProcessor() {
                            @Override
                            public String process(String mustacheName,
                                    String mustacheSource) {
                                return new StringBuilder().append("<source>")
                                        .append(mustacheSource)
                                        .append("</source>").toString();
                            }
                        }).build())
                .addTemplateLocator(
                        new MapTemplateLocator(ImmutableMap.of("template",
                                "{{foo}}"))).build();
        assertEquals(
                "<source>{{foo}}</source>",
                engine.compileMustache("embed_helper02", "{{embed 'template'}}")
                        .render(null));
    }

}
