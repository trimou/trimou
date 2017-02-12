package org.trimou.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.MapTemplateLocator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public final class SpecUtils {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SpecUtils.class);

    /**
     * Execute all the spec tests in the given JSON file.
     *
     * @param filename
     * @param specVersion
     * @throws IOException
     */
    static void executeTests(String filename, String specVersion)
            throws IOException {
        executeTests(filename, specVersion, null);
    }

    static void executeTests(String filename, String specVersion,
            String singleTest) throws IOException {

        List<Definition> definitions = parseDefinitions(getSpecFile(filename,
                specVersion));

        if (!definitions.isEmpty()) {

            int idx = 0;
            int failures = 0;

            for (Definition definition : definitions) {

                if (singleTest != null
                        && !singleTest.equals(definition.getName())) {
                    continue;
                }

                // Mock partials
                MapTemplateLocator mockTemplateLocator = new MapTemplateLocator(
                        definition.getPartials());

                MustacheEngine factory = MustacheEngineBuilder
                        .newBuilder()
                        .addTemplateLocator(mockTemplateLocator)
                        .setProperty(
                                EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED,
                                false).build();

                idx++;

                try {

                    assertEquals(
                            definition.getExpected(),
                            factory.compileMustache(definition.getName(),
                                    definition.getTemplate()).render(
                                    definition.getData()));

                } catch (Exception e) {
                    failures++;
                    LOGGER.error("{} {}: {} - {}", idx, definition.getName(),
                            e.getClass(), e.getMessage());
                } catch (Error e) {
                    failures++;
                    LOGGER.error("{} {}: {} - {}", idx, definition.getName(),
                            e.getClass(), e.getMessage());
                }
            }
            // Use warn log level so that this message is visible during
            // ordinary build (warn is the default log level)
            LOGGER.warn("Spec tests finished [filename: {}, tests: {}, failures: {}]", filename, definitions.size(),
                    failures);
            if (failures > 0) {
                fail(String.format("Spec tests failures: %s", failures));
            }
        }
    }

    static Reader getSpecFile(String filename, String specVersion) {
        return new InputStreamReader(
                SpecUtils.class.getResourceAsStream("/spec/" + specVersion
                        + "/" + filename));
    }

    static List<Definition> parseDefinitions(Reader reader) throws IOException {

        List<Definition> definitions = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement spec = parser.parse(reader);
        reader.close();
        JsonArray tests = spec.getAsJsonObject().get("tests").getAsJsonArray();

        for (JsonElement test : tests) {

            JsonObject testObject = test.getAsJsonObject();

            Definition definition = new Definition();
            definition.setName(testObject.get("name").getAsString());
            definition.setDesc(testObject.get("desc").getAsString());
            definition.setTemplate(testObject.get("template").getAsString());
            definition.setExpected(testObject.get("expected").getAsString());

            Map<String, Object> data = new HashMap<>();
            JsonObject dataObject = testObject.get("data").getAsJsonObject();
            for (Entry<String, JsonElement> property : dataObject.entrySet()) {
                if (!property.getKey().equals("lambda")) {
                    data.put(property.getKey(),
                            getJsonElementValue(property.getValue()));
                }
            }
            if (Lambdas.testMap.containsKey(definition.getName())) {
                data.put("lambda", Lambdas.testMap.get(definition.getName()));
            }
            definition.setData(data);

            if (testObject.has("partials")) {
                JsonObject partialsObject = testObject.get("partials")
                        .getAsJsonObject();
                Map<String, String> partials = new HashMap<>();
                for (Entry<String, JsonElement> entry : partialsObject
                        .entrySet()) {
                    partials.put(entry.getKey(), entry.getValue().getAsString());
                }
                definition.setPartials(partials);
            }

            definitions.add(definition);
        }
        return definitions;
    }

    private static Object getJsonElementValue(JsonElement element) {

        if (element.isJsonPrimitive()) {
            return getJsonPrimitiveElementValue(element);
        } else if (element.isJsonArray()) {
            return getJsonArrayElementValue(element);
        } else if (element.isJsonObject()) {
            Map<String, Object> objectData = new HashMap<>();
            for (Entry<String, JsonElement> objectProperty : element
                    .getAsJsonObject().entrySet()) {
                objectData.put(objectProperty.getKey(),
                        getJsonElementValue(objectProperty.getValue()));
            }
            return objectData;
        } else if (element.isJsonNull()) {
            return null;
        }
        throw new IllegalStateException("Unsupported JSON element");
    }

    private static Object getJsonPrimitiveElementValue(JsonElement element) {

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else if (primitive.isString()) {
            return primitive.getAsString();
        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();
        } else {
            throw new IllegalStateException("Unsupported primitive type");
        }
    }

    private static Object getJsonArrayElementValue(JsonElement element) {

        JsonArray array = element.getAsJsonArray();
        List<Object> values = new ArrayList<>(array.size());
        for (JsonElement jsonElement : array) {
            values.add(getJsonElementValue(jsonElement));
        }
        return values;
    }

}
