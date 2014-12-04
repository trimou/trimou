package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;
import org.trimou.Mustache;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class InvertedSectionSegmentTest extends AbstractEngineTest {

    @Test
    public void testBoolean() {
        String templateContents = "Hello {{^test}}me{{/test}}!";
        Mustache mustache = engine.compileMustache("boolean", templateContents);
        assertEquals("Hello me!", mustache.render(ImmutableMap
                .<String, Object> of("test", false)));
        assertEquals("Hello !",
                mustache.render(ImmutableMap.<String, Object> of("test", true)));
    }

    @Test
    public void testIterable() {
        String templateContents = "{{^numbers}}Hey!{{/numbers}}";
        Mustache mustache = engine
                .compileMustache("iterable", templateContents);
        assertEquals("Hey!", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", Collections.emptyList())));
        assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", Collections.singleton(1))));
    }

    @Test
    public void testArray() {
        String templateContents = "{{^numbers}}Hey!{{/numbers}}";
        Mustache mustache = engine
                .compileMustache("iterable", templateContents);
        assertEquals("Hey!", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", new Object[] {})));
        assertEquals("", mustache.render(ImmutableMap.<String, Object> of(
                "numbers", new String[] { "Hello" })));
    }

    @Test
    public void testNestedContext() {
        String templateContents = "Hello {{^test}}ping{{/test}}!";
        Mustache mustache = engine.compileMustache("nested", templateContents);
        assertEquals("Hello !", mustache.render(ImmutableMap
                .<String, Object> of("test", new Hammer())));
        assertEquals("Hello ping!",
                mustache.render(Collections.singletonMap("test", null)));
    }

    @Test
    public void testNumber() {
        String templateContents = "Hello {{^this}}ping{{/this}}!";
        Mustache mustache = engine.compileMustache("number", templateContents);
        assertEquals("Hello !", mustache.render(new BigDecimal("0.1")));
        assertEquals("Hello !", mustache.render(BigDecimal.ZERO));
        assertEquals("Hello !", mustache.render(Long.valueOf(0l)));
    }

}
