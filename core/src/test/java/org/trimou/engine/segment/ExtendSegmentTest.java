package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ExtendSegmentTest extends AbstractEngineTest {

    @Before
    public void buildEngine() {
    }

    @Test
    public void testSimpleInheritance() {
        MapTemplateLocator locator = new MapTemplateLocator(
                ImmutableMap
                        .of("super", "Hello {{$insert}}Martin{{/insert}}",
                                "sub",
                                "And now... {{<super}} {{$insert}}{{name}}{{/insert}} {{/super}}!"));
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();
        Mustache sub = engine.getMustache("sub");
        assertEquals("And now... Hello Edgar!",
                sub.render(ImmutableMap.<String, Object> of("name", "Edgar")));
    }

    @Test
    public void testMultipleInheritance() {
        MapTemplateLocator locator = new MapTemplateLocator(
                ImmutableMap
                        .of("super",
                                "for {{$insert}}{{/insert}}",
                                "sub",
                                "And now {{<super}} {{$insert}}something {{$insert2}}{{/insert2}} different{{/insert}} {{/super}}.",
                                "subsub",
                                "{{<sub}} {{$insert2}}completely{{/insert2}} {{/sub}}"));
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();
        Mustache sub = engine.getMustache("subsub");
        assertEquals("And now for something completely different.",
                sub.render(null));
    }

    @Test
    public void testMultipleInheritanceOverride() {
        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "super", "{{$insert}}{{/insert}}", "sub",
                "{{<super}} {{$insert}}false{{/insert}} {{/super}}", "subsub",
                "{{<sub}} {{$insert}}true{{/insert}} {{/sub}}"));
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();
        Mustache sub = engine.getMustache("subsub");
        assertEquals("true", sub.render(null));
    }

    @Test
    public void testRecursiveInvocationAllowed() {
        MapTemplateLocator locator = new MapTemplateLocator(
                ImmutableMap
                        .of("super",
                                "{{$content}}{{<super}}{{$content}}Mooo{{/content}}{{/super}}{{/content}}"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        5).build();
        assertEquals("Mooo", engine.getMustache("super").render(null));
    }

    @Test
    public void testRecursiveInvocationDisabled() {
        MapTemplateLocator locator = new MapTemplateLocator(
                ImmutableMap
                        .of("super",
                                "{{$content}}{{<super}}{{$content}}Mooo{{/content}}{{/super}}{{/content}}"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        0).build();

        try {
            engine.getMustache("super").render(null);
            fail("Limit exceeded and no exception thrown");
        } catch (MustacheException e) {
            if (!e.getCode()
                    .equals(MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
            // else {
            // e.printStackTrace();
            // }
        }
    }

    @Test
    public void testRecursiveInvocationLimitExceeded() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "super", "/n{{<super}}{{/super}}"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        5).build();

        try {
            engine.getMustache("super").render(null);
            fail("Limit exceeded and no exception thrown");
        } catch (MustacheException e) {
            if (!e.getCode()
                    .equals(MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
            // else {
            // e.printStackTrace();
            // }
        }
    }

    @Test
    public void testExtendNotFound() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();

        try {
            engine.compileMustache("extend_not_found",
                    "Hello,\nan attempt to extend \n\n {{<neverexisted}}{{/neverexisted}}")
                    .render(null);
            fail("Template to extend does not exist!");
        } catch (MustacheException e) {
            if (!e.getCode().equals(MustacheProblem.RENDER_INVALID_EXTEND_KEY)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
        }
    }

}
