package org.trimou.engine.convert;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class ValueConverterTest extends AbstractTest {

    @Test
    public void testValueConverter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter((v) -> v.toString().toUpperCase()).build();
        assertEquals("FOO", engine
                .compileMustache("value_converter", "{{this}}").render("Foo"));
    }

    @Test
    public void testInvalidValueConverter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter(new ValueConverter() {

                    @Override
                    public String convert(Object from) {
                        return from.toString().toUpperCase();
                    }

                    @Override
                    public boolean isValid() {
                        return false;
                    }
                }).build();
        assertEquals("Foo",
                engine.compileMustache("value_converter_invalid", "{{this}}")
                        .render("Foo"));
    }

    @Test
    public void testValueConverterOrdering() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter(new ValueConverter() {

                    @Override
                    public String convert(Object from) {
                        return from.toString().toUpperCase();
                    }

                    @Override
                    public int getPriority() {
                        return 1;
                    }

                }).addValueConverter(new ValueConverter() {

                    @Override
                    public String convert(Object from) {
                        return from.toString().toLowerCase();
                    }

                    @Override
                    public int getPriority() {
                        return 2;
                    }

                }).build();
        assertEquals("foo",
                engine.compileMustache("value_converter_ordering", "{{this}}")
                        .render("Foo"));
    }

}
