/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.gson.resolver;

import static org.trimou.engine.priority.Priorities.rightBefore;

import java.util.Collections;
import java.util.Set;

import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.resolver.IndexResolver;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.Placeholder;
import org.trimou.engine.resolver.ResolutionContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Gson's parser API resolver.
 *
 * @author Martin Kouba
 * @see <a href="http://code.google.com/p/google-gson/">http://code.google.com/p
 *      /google-gson/</a>
 */
public class JsonElementResolver extends IndexResolver {

    public static final int JSON_ELEMENT_RESOLVER_PRIORITY = rightBefore(
            MapResolver.MAP_RESOLVER_PRIORITY);

    /**
     * Use this name if you want to unwrap the current context object (note that
     * "this" would be normally matched by ThisResolver)
     */
    public static final String NAME_UNWRAP_THIS = "unwrapThis";

    /**
     * If set to <code>true</code> instances of JsonPrimitive and JsonNull are
     * unwrapped automatically.
     */
    public static final ConfigurationKey UNWRAP_JSON_PRIMITIVE_KEY = new SimpleConfigurationKey(
            JsonElementResolver.class.getName() + ".unwrapJsonPrimitive", true);

    private boolean unwrapJsonPrimitive;

    private final Hint hint;

    /**
     *
     */
    public JsonElementResolver() {
        this(JSON_ELEMENT_RESOLVER_PRIORITY);
    }

    /**
     * @param priority
     */
    public JsonElementResolver(int priority) {
        super(priority);
        this.hint = new Hint() {
            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                return JsonElementResolver.this.resolve(contextObject, name,
                        context);
            }
        };
    }

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null || !(contextObject instanceof JsonElement)) {
            return null;
        }

        JsonElement element = (JsonElement) contextObject;

        if (element.isJsonArray() && isAnIndex(name)) {
            // Index-based access of JsonArray elements
            JsonArray jsonArray = (JsonArray) element;
            // #26 Unwrap the element if necessary
            final Integer index = getIndexValue(name, context.getKey(),
                    jsonArray.size());
            if (index != null) {
                return unwrapJsonElementIfNecessary(jsonArray.get(index));
            }
        } else if (element.isJsonObject()) {
            // JsonObject properties
            JsonObject jsonObject = (JsonObject) element;
            JsonElement member = jsonObject.get(name);
            if (member != null) {
                return unwrapJsonElementIfNecessary(member);
            }
        } else if (name.equals(NAME_UNWRAP_THIS)) {
            return unwrapJsonElementIfNecessary(element);
        }
        return null;
    }

    @Override
    public void init() {
        unwrapJsonPrimitive = configuration
                .getBooleanPropertyValue(UNWRAP_JSON_PRIMITIVE_KEY);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(UNWRAP_JSON_PRIMITIVE_KEY);
    }

    @Override
    public Hint createHint(Object contextObject, String name,
            ResolutionContext context) {
        return hint;
    }

    private Object unwrapJsonElementIfNecessary(JsonElement jsonElement) {
        if (unwrapJsonPrimitive) {
            if (jsonElement.isJsonPrimitive()) {
                return unwrapJsonPrimitive((JsonPrimitive) jsonElement);
            } else if (jsonElement.isJsonNull()) {
                return Placeholder.NULL;
            }
        }
        return jsonElement;
    }

    private Object unwrapJsonPrimitive(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        } else if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        } else if (jsonPrimitive.isNumber()) {
            return jsonPrimitive.getAsNumber();
        }
        return jsonPrimitive;
    }

}
