package org.trimou.gson.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Martin Kouba
 */
public class JsonElementResolverTest {

    @Test
    public void testResolution() {
        JsonElementResolver resolver = new JsonElementResolver();

        // Init the resolver
        MustacheEngineBuilder.newBuilder().addResolver(resolver).build();

        assertNull(resolver.resolve(null, "foo", null));
        assertNull(resolver.resolve("bar", "foo", null));
        assertNotNull(resolver.resolve(new JsonPrimitive(true), "whatever",
                null));
    }

    @Test
    public void testInterpolation() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addResolver(new JsonElementResolver()).build();
        Mustache mustache = engine
                .compileMustache(
                        "json_element_test",
                        "{{lastName}}|{{address.street}}|{{#phoneNumbers}}{{type}}{{#iterHasNext}},{{/iterHasNext}}{{/phoneNumbers}}|{{phoneNumbers.0.type}}");
        assertEquals("Novy|Nova|home,mobile|home", mustache.render(loadJsonData()));
    }

    @Test
    public void testUnwrapJsonPrimitiveSetToFalse() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addResolver(new JsonElementResolver())
                .setProperty(JsonElementResolver.UNWRAP_JSON_PRIMITIVE_KEY,
                        false).build();
        Mustache mustache = engine
                .compileMustache(
                        "json_element_unwrap_primitive_disabled_test",
                        "{{firstName.asString.length}}|{{phoneNumbers.1.type.asString.toUpperCase}}");
        assertEquals("3|MOBILE", mustache.render(loadJsonData()));
    }

    private JsonElement loadJsonData() throws JsonIOException, JsonSyntaxException,
            FileNotFoundException {
        return new JsonParser().parse(new FileReader(new File(
                "src/test/resources/data.json")));
    }

}
