package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Executors;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class AsyncHelperTest extends AbstractTest {

    @Test
    public void testAsyncHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setExecutorService(Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors()))
                .registerHelpers(
                        HelpersBuilder.empty().addAsync().addInclude().build())
                .addTemplateLocator(new MapTemplateLocator(
                        ImmutableMap.of("template", "async")))
                .build();
        assertEquals("Hello async world!!",
                engine.compileMustache("async_helper01",
                        "Hello {{#async}}{{include templateName}} {{world}}{{/async}}!")
                        .render(ImmutableMap.of("templateName", "template",
                                "world", "world!")));
    }

    @Test
    public void testAsyncHelperNested() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setExecutorService(Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors()))
                .registerHelpers(
                        HelpersBuilder.empty().addAsync().addInclude().build())
                .addTemplateLocator(new MapTemplateLocator(
                        ImmutableMap.of("template", "async")))
                .build();
        assertEquals("Hello async world! No!",
                engine.compileMustache("async_helper02",
                        "Hello {{#async}}{{include templateName}} {{#async}}{{#async}}{{world}}{{/async}}{{/async}}{{#async}}!{{/async}}{{/async}}{{#async}} No{{/async}}!")
                        .render(ImmutableMap.of("templateName", "template",
                                "world", "world")));
    }

    @Test
    public void testAsyncHelperNeedsExecutorService() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addAsync().build())
                .build();
        MustacheExceptionAssert
                .expect(MustacheProblem.RENDER_ASYNC_PROCESSING_ERROR).check(
                        () -> engine
                                .compileMustache("async_helper03",
                                        "{{#async}}foo{{/async}}")
                                .render(null));
    }

}
