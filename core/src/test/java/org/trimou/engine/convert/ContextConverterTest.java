package org.trimou.engine.convert;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class ContextConverterTest extends AbstractTest {

    @Test
    public void testContextConverter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addContextConverter((v) -> v instanceof String ? v.toString().length() : null).build();
        Mustache mustache = engine.compileMustache("{{this}}");
        assertEquals("3", mustache.render("Foo"));
        assertEquals("1", mustache.render(1));
    }

    @Test
    public void testInvalidContextConverter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().addContextConverter(new ContextConverter() {

            @Override
            public Object convert(Object from) {
                return "true";
            }

            @Override
            public boolean isValid() {
                return false;
            }
        }).build();
        assertEquals("Foo", engine.compileMustache("{{this}}").render("Foo"));
    }

    @Test
    public void testContextConverterOrdering() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().addContextConverter(new ContextConverter() {

            @Override
            public Object convert(Object from) {
                return from.toString().toUpperCase();
            }

            @Override
            public int getPriority() {
                return 1;
            }

            public String toString() {
                return "P1";
            }

        }).addContextConverter(new ContextConverter() {

            @Override
            public Object convert(Object from) {
                return from.toString().toLowerCase();
            }

            @Override
            public int getPriority() {
                return 2;
            }

            public String toString() {
                return "P2";
            }

        }).build();
        assertEquals("foo", engine.compileMustache("{{this}}").render("Foo"));
    }

}
