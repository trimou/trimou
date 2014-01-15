package org.trimou.gson.resolver;

import static org.junit.Assert.assertEquals;
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
        assertEquals(Boolean.TRUE, resolver.resolve(new JsonPrimitive(true), "unwrapThis", null));
        assertNull(resolver.resolve(new JsonPrimitive(true), "whatever", null));
    }

    @Test
    public void testInterpolation() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {
        Mustache mustache = getEngine()
                .compileMustache(
                        "json_element_test",
                        "{{lastName}}|{{address.street}}|{{#phoneNumbers}}{{type}}{{#iterHasNext}},{{/iterHasNext}}{{/phoneNumbers}}|{{phoneNumbers.0.type}}");
        assertEquals("Novy|Nova|home,mobile|home",
                mustache.render(loadJsonData()));
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
                .compileMustache("json_element_unwrap_primitive_disabled_test",
                        "{{firstName.asString.length}}|{{phoneNumbers.1.type.asString.toUpperCase}}");
        assertEquals("3|MOBILE", mustache.render(loadJsonData()));
    }

    @Test
    public void testUnwrapJsonArrayElementAtIndex() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {
        // https://github.com/trimou/trimou/issues/26
        String json = "{users: [\"izeye\", \"always19\"]}";
        String template = "One of users is {{users.0}}.";
        JsonElement jsonElement = new JsonParser().parse(json);
        MustacheEngine engine = getEngine();
        Mustache mustache = engine.compileMustache("unwrap_array_index",
                template);
        assertEquals("One of users is izeye.", mustache.render(jsonElement));
    }

    @Test
    public void testUnwrapJsonArray() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {

        MustacheEngine engine = getEngine();
        assertEquals(
                "Jim,true,5",
                engine.compileMustache("json_unwrap_array_element_test",
                        "{{#aliases}}{{unwrapThis}}{{#iterHasNext}},{{/iterHasNext}}{{/aliases}}")
                        .render(loadJsonData()));
        assertEquals(
                "Jim,true,5",
                engine.compileMustache("json_unwrap_array_element_test2",
                        "{{#this}}{{unwrapThis}}{{#iterHasNext}},{{/iterHasNext}}{{/this}}")
                        .render(loadJsonData("data_array.json")));
    }

    private MustacheEngine getEngine() {
        return MustacheEngineBuilder.newBuilder()
                .addResolver(new JsonElementResolver()).build();
    }

    private JsonElement loadJsonData() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {
        return loadJsonData("data.json");
    }

    private JsonElement loadJsonData(String fileName) throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {
        return new JsonParser().parse(new FileReader(new File(
                "src/test/resources/" + fileName)));
    }

}
