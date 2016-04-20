package org.trimou.jsonp.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.math.BigDecimal;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Mustache;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.interpolation.ThrowingExceptionMissingValueHandler;
import org.trimou.engine.resolver.DummyResolutionContext;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.Placeholder;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.ThisResolver;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class JsonValueResolverTest extends AbstractTest {

    @Test
    public void testResolution() {
        JsonValueResolver resolver = new JsonValueResolver();
        ResolutionContext ctx = new DummyResolutionContext();
        // Init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();
        assertNull(resolver.resolve(null, "foo", ctx));
        assertNull(resolver.resolve("bar", "foo", ctx));
        assertEquals(Boolean.TRUE,
                resolver.resolve(JsonValue.TRUE, "unwrapThis", ctx));
        assertNull(resolver.resolve(JsonValue.TRUE, "whatever", ctx));
        assertEquals(Placeholder.NULL,
                resolver.resolve(JsonValue.NULL, "unwrapThis", ctx));
        JsonObject jsonObject = Json.createObjectBuilder().add("foo", "bar")
                .add("baz", true).build();
        assertEquals("bar", resolver.resolve(jsonObject, "foo", ctx));
        assertEquals(Boolean.TRUE, resolver.resolve(jsonObject, "baz", ctx));
        JsonArray jsonArray = Json.createArrayBuilder().add(true).add(1)
                .build();
        assertEquals(true, resolver.resolve(jsonArray, "0", ctx));
        assertEquals(BigDecimal.ONE, resolver.resolve(jsonArray, "1", ctx));
    }

    @Test
    public void testInterpolation() throws FileNotFoundException {
        Mustache mustache = getEngine().compileMustache("json_value_test",
                "{{lastName}}|{{address.street}}|{{#phoneNumbers}}{{type}}{{#iterHasNext}},{{/iterHasNext}}{{/phoneNumbers}}|{{phoneNumbers.0.type}}");
        assertEquals("Novy|Nova|home,mobile|home",
                mustache.render(loadJsonData()));
    }

    @Test
    public void testUnwrapJsonArrayElementAtIndex()
            throws FileNotFoundException {
        // https://github.com/trimou/trimou/issues/26
        String json = "{\"users\": [\"izeye\", \"always19\"]}";
        String template = "One of users is {{users.0}}.";
        JsonStructure jsonStructure = Json.createReader(new StringReader(json))
                .read();
        MustacheEngine engine = getEngine();
        Mustache mustache = engine.compileMustache("unwrap_array_index",
                template);
        assertEquals("One of users is izeye.", mustache.render(jsonStructure));
    }

    @Test
    public void testOutOfBoundIndexException() throws FileNotFoundException {
        // https://github.com/trimou/trimou/issues/73
        String json = "{\"numbers\": [1,2]}";
        String template = "One of users is {{numbers.2}}.";
        JsonStructure jsonStructure = Json.createReader(new StringReader(json))
                .read();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setMissingValueHandler(
                        new ThrowingExceptionMissingValueHandler())
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(new ThisResolver()).addResolver(new MapResolver())
                .addResolver(new JsonValueResolver()).build();
        final Mustache mustache = engine.compileMustache("unwrap_array_index",
                template);
        MustacheExceptionAssert.expect(MustacheProblem.RENDER_NO_VALUE)
                .check(new Runnable() {
                    @Override
                    public void run() {
                        mustache.render(jsonStructure);
                    }
                });
    }

    @Test
    public void testUnwrapJsonArray() throws FileNotFoundException {
        MustacheEngine engine = getEngine();
        assertEquals("Jim,true,5",
                engine.compileMustache("json_unwrap_array_element_test",
                        "{{#aliases}}{{unwrapThis}}{{#iterHasNext}},{{/iterHasNext}}{{/aliases}}")
                        .render(loadJsonData()));
        assertEquals("Jim,true,5",
                engine.compileMustache("json_unwrap_array_element_test2",
                        "{{#this}}{{unwrapThis}}{{#iterHasNext}},{{/iterHasNext}}{{/this}}")
                        .render(loadJsonData("data_array.json")));
    }

    @Test
    public void testUnwrapJsonNull() throws FileNotFoundException {
        MustacheEngine engine = getEngine();
        assertEquals("Jimtrue",
                engine.compileMustache("json_unwrap_null_test1",
                        "{{#this}}{{unwrapThis}}{{/this}}")
                        .render(loadJsonData("data_array_with_null.json")));
        assertEquals("",
                engine.compileMustache("json_unwrap_null_test2",
                        "{{firstName}}")
                        .render(loadJsonData("data_array_with_null.json")));
    }

    private MustacheEngine getEngine() {
        return MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(new ThisResolver()).addResolver(new MapResolver())
                .addResolver(new JsonValueResolver()).build();
    }

    private JsonStructure loadJsonData() throws FileNotFoundException {
        return loadJsonData("data.json");
    }

    private JsonStructure loadJsonData(String fileName)
            throws FileNotFoundException {
        return Json.createReader(
                new FileReader(new File("src/test/resources/" + fileName)))
                .read();
    }

}
