/*
 * Copyright 2016 Martin Kouba
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
package org.trimou.jsonp.resolver;

import static org.trimou.engine.priority.Priorities.rightBefore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.resolver.IndexResolver;
import org.trimou.engine.resolver.MapResolver;
import org.trimou.engine.resolver.Placeholder;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.validation.Validateable;

/**
 * JSON Processing Object Model API (JSR 353) resolver. Since {@link JsonObject}
 * implements {@link Map} and {@link JsonArray} implements {@link List} this
 * resolver is only useful if automatic unwrapping is required. Automatic
 * unwrapping means resolving {@link JsonString#getString()} for
 * {@link JsonString}, {@link JsonNumber#bigDecimalValue()} for
 * {@link JsonNumber}, {@link Boolean#TRUE} for {@link JsonValue#TRUE},
 * {@link Boolean#FALSE} for {@link JsonValue#FALSE} and
 * {@link Placeholder#NULL} for {@link JsonValue#NULL}.
 * <p>
 * This resolver should always have higher priority than {@link MapResolver} to
 * be able to process instances of {@link JsonObject}.
 *
 * @author Martin Kouba
 */
public class JsonValueResolver extends IndexResolver implements Validateable {

    public static final int JSON_VALUE_RESOLVER_PRIORITY = rightBefore(
            MapResolver.MAP_RESOLVER_PRIORITY);

    public static final ConfigurationKey ENABLED_KEY = new SimpleConfigurationKey(
            JsonValueResolver.class.getName() + ".enabled", false);

    /**
     * Use this name if you want to unwrap the current context object (note that
     * "this" would be normally matched by ThisResolver)
     */
    public static final String NAME_UNWRAP_THIS = "unwrapThis";

    private final Hint hint;

    protected volatile boolean isEnabled;

    public JsonValueResolver() {
        this(JSON_VALUE_RESOLVER_PRIORITY);
    }

    /**
     * @param priority
     */
    public JsonValueResolver(int priority) {
        super(priority);
        this.hint = JsonValueResolver.this::resolve;
    }

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null || !(contextObject instanceof JsonValue)) {
            return null;
        }

        JsonValue jsonValue = (JsonValue) contextObject;

        if (ValueType.ARRAY.equals(jsonValue.getValueType())
                && isAnIndex(name)) {
            // Index-based access of JsonArray elements
            JsonArray jsonArray = (JsonArray) jsonValue;
            // #26 Unwrap the element if necessary
            final Integer index = getIndexValue(name, context.getKey(),
                    jsonArray.size());
            if (index != null) {
                return unwrapJsonValueIfNecessary(jsonArray.get(index));
            }
        } else if (ValueType.OBJECT.equals(jsonValue.getValueType())) {
            // JsonObject properties
            JsonObject jsonObject = (JsonObject) jsonValue;
            JsonValue member = jsonObject.get(name);
            if (member != null) {
                return unwrapJsonValueIfNecessary(member);
            }
        } else if (name.equals(NAME_UNWRAP_THIS)) {
            return unwrapJsonValueIfNecessary(jsonValue);
        }
        return null;
    }

    @Override
    public Hint createHint(Object contextObject, String name,
            ResolutionContext context) {
        return hint;
    }

    @Override
    public void init(Configuration configuration) {
        isEnabled = configuration.getBooleanPropertyValue(ENABLED_KEY);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ENABLED_KEY);
    }

    private Object unwrapJsonValueIfNecessary(JsonValue jsonValue) {
        switch (jsonValue.getValueType()) {
        case STRING:
            return ((JsonString) jsonValue).getString();
        case NUMBER:
            return ((JsonNumber) jsonValue).bigDecimalValue();
        case TRUE:
            return Boolean.TRUE;
        case FALSE:
            return Boolean.FALSE;
        case NULL:
            return Placeholder.NULL;
        default:
            return jsonValue;
        }

    }

}
