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
package org.trimou.gson.converter;

import java.util.Collections;
import java.util.Set;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.convert.AbstractValueConverter;
import org.trimou.util.Strings;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

/**
 * Converts {@link JsonPrimitive} and {@link JsonNull}.
 *
 * @author Martin Kouba
 */
public class GsonValueConverter extends AbstractValueConverter {

    public static final ConfigurationKey ENABLED_KEY = new SimpleConfigurationKey(
            GsonValueConverter.class.getName() + ".enabled", true);

    public GsonValueConverter() {
        super();
    }

    public GsonValueConverter(int priority) {
        super(priority);
    }

    @Override
    public String convert(Object value) {
        if (value instanceof JsonPrimitive) {
            return ((JsonPrimitive) value).getAsString();
        } else if (value instanceof JsonNull) {
            return Strings.EMPTY;
        }
        return null;
    }

    @Override
    public void init(Configuration configuration) {
        isEnabled = configuration.getBooleanPropertyValue(ENABLED_KEY);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ENABLED_KEY);
    }

}
