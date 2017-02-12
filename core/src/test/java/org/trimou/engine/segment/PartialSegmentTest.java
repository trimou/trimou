package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

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
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class PartialSegmentTest extends AbstractEngineTest {

    @Before
    public void buildEngine() {
    }

    @Test
    public void testRecursiveInvocationLimitExceeded() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "part", "{{>part}}", "alpha", "{{>bravo}}", "bravo",
                "{{>alpha}}"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        5).build();

        // Simply test infinite loops
        try {
            engine.getMustache("part").render(null);
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

        try {
            engine.getMustache("alpha").render(null);
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
    public void testRecursiveInvocationDisabled() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "node", "{{content}}<{{#nodes}}{{>node}}{{/nodes}}>"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        0).build();

        Map<String, Object> data = ImmutableMap.of("content",
                "X", "nodes", new Map[] { ImmutableMap.of("content", "Y",
                        "nodes", new Map[] { ImmutableMap.of("content", "Z",
                                "nodes", new Map[] {}) }) });

        try {
            engine.getMustache("node").render(data);
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
    public void testRecursiveInvocationAllowed() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "node", "{{content}}<{{#nodes}}{{>node}}{{/nodes}}>"));
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();

        Map<String, Object> data = ImmutableMap.of("content",
                "X", "nodes", new Map[] { ImmutableMap.of("content", "Y",
                        "nodes", new Map[] { ImmutableMap.of("content", "Z",
                                "nodes", new Map[] {}) }) });

        assertEquals("X<Y<Z<>>>", engine.getMustache("node").render(data));
    }

    @Test
    public void testPartialNotFound() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();

        try {
            engine.compileMustache("partial_not_found",
                    "Hello,\n this partial \n\n {{>neverexisted}}")
                    .render(null);
            fail("Partial does not exist!");
        } catch (MustacheException e) {
            if (!e.getCode().equals(MustacheProblem.RENDER_INVALID_PARTIAL_KEY)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testTemplateInvocationsStack() {
        // See also bug #42
        String partial = "!";
        StringBuilder template = new StringBuilder();
        StringBuilder result = new StringBuilder();
        int loop = 2 * Integer
                .valueOf(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT
                        .getDefaultValue().toString());
        for (int i = 0; i < loop; i++) {
            template.append("{{> partial}}");
            result.append(partial);
        }
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(
                        new MapTemplateLocator(ImmutableMap.of("template",
                                template.toString(), "partial",
                                partial.toString()))).build();
        assertEquals(result.toString(),
                engine.getMustache("template").render(null));
    }

    @Test
    public void testCachedPartialSegmentUsed() {
        Map<String, String> map = new HashMap<>();
        map.put("alpha", "{{>bravo}}");
        map.put("bravo", "{{this}}");
        MapTemplateLocator locator = new MapTemplateLocator(map);
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();
        Mustache mustache = engine.getMustache("alpha");
        assertEquals("foo", mustache.render("foo"));
        map.put("bravo", "NOTHING");
        engine.invalidateTemplateCache();
        assertEquals("foo", mustache.render("foo"));
    }

    @Test
    public void testCachedPartialSegmentNotUsed() {
        Map<String, String> map = new HashMap<>();
        map.put("alpha", "{{>bravo}}");
        map.put("bravo", "{{this}}");
        MapTemplateLocator locator = new MapTemplateLocator(map);
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator)
                .setProperty(EngineConfigurationKey.DEBUG_MODE, true).build();
        Mustache mustache = engine.getMustache("alpha");
        assertEquals("foo", mustache.render("foo"));
        map.put("bravo", "NOTHING");
        assertEquals("NOTHING", mustache.render("foo"));
    }

}
