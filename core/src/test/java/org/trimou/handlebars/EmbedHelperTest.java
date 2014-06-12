package org.trimou.handlebars;


import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;

import static org.junit.Assert.assertEquals;

/**
 * @author Minkyu Cho
 */
public class EmbedHelperTest extends AbstractTest {
    @Test
    public void testEmbeddedHelper() {
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(HelpersBuilder.empty().addEmbed().build())
                .addTemplateLocator(new MapTemplateLocator(ImmutableMap.of("template", "Hello!")))
                .build();
        assertEquals("<script id=\"template\" type=\"text/template\">\nHello!\n</script>",
                engine.compileMustache("embed_helper01", "{{embed this}}")
                        .render("template"));
    }
}
