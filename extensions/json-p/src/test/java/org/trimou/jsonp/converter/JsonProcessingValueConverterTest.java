package org.trimou.jsonp.converter;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.json.Json;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.jsonp.resolver.JsonValueResolver;

/**
 *
 * @author Martin Kouba
 */
public class JsonProcessingValueConverterTest extends AbstractTest {

    @Test
    public void testConverter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter(new JsonProcessingValueConverter())
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, true)
                .setProperty(JsonValueResolver.ENABLED_KEY, false)
                .setProperty(JsonProcessingValueConverter.ENABLED_KEY, true)
                .build();
        Mustache mustache = engine.compileMustache("converter_test",
                "{{#this}}{{.}}{{/this}}");
        assertEquals("1true", mustache.render(Json
                .createReader(new StringReader("[\"1\",true,null]")).read()));
    }

    @Test
    public void testUnwrapJsonPrimitiveSetToFalse() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addValueConverter(new JsonProcessingValueConverter())
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, true)
                .setProperty(JsonValueResolver.ENABLED_KEY, false)
                .setProperty(JsonProcessingValueConverter.ENABLED_KEY, false)
                .build();
        Mustache mustache = engine.compileMustache(
                "converter_unwrap_primitive_disabled_test",
                "{{#this}}{{.}}{{/this}}");
        assertEquals("\"1\"truenull", mustache.render(Json
                .createReader(new StringReader("[\"1\",true,null]")).read()));
    }

}
