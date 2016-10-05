package org.trimou.gson.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.gson.resolver.JsonElementResolver;

import com.google.gson.JsonParser;

/**
 *
 * @author Martin Kouba
 */
public class GsonValueConverterTest extends AbstractTest {

    @Test
    public void testConverter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter(new GsonValueConverter())
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, true)
                .setProperty(JsonElementResolver.UNWRAP_JSON_PRIMITIVE_KEY,
                        true)
                .build();
        Mustache mustache = engine.compileMustache("converter_test",
                "{{#this}}{{this}}{{isJsonPrimitive}}{{/this}}");
        assertEquals("1true",
                mustache.render(new JsonParser().parse("[\"1\"]")));
    }

    @Test
    public void testUnwrapJsonPrimitiveSetToFalse() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter(new GsonValueConverter())
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, true)
                .setProperty(GsonValueConverter.ENABLED_KEY, false).build();
        Mustache mustache = engine.compileMustache(
                "converter_unwrap_primitive_disabled_test",
                "{{#this}}{{this}}{{isJsonPrimitive}}{{/this}}");
        assertEquals("\"1\"true",
                mustache.render(new JsonParser().parse("[\"1\"]")));
    }

}
