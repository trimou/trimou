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
package org.trimou.engine.config;

import org.trimou.engine.convert.Converter;
import org.trimou.util.Checker;

/**
 *
 * @author Martin Kouba
 */
public class SimpleConfigurationKey implements ConfigurationKey {

    private final String key;

    private final Object defaultValue;

    private final Converter<Object, Object> converter;

    /**
     *
     * @param key
     * @param defaultValue
     */
    public SimpleConfigurationKey(String key, Object defaultValue) {
        this(key, defaultValue, null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @param converter
     */
    public SimpleConfigurationKey(String key, Object defaultValue, Converter<Object, Object> converter) {
        Checker.checkArgumentsNotNull(key, defaultValue);
        this.key = key;
        this.defaultValue = defaultValue;
        this.converter = converter;
    }

    @Override
    public String get() {
        return key;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Converter<Object, Object> getConverter() {
        return converter != null ? converter : ConfigurationKey.super.getConverter();
    }

    @Override
    public String toString() {
        return String.format("SimpleConfigurationKey [key:%s, defaultValue: %s]", key, defaultValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleConfigurationKey other = (SimpleConfigurationKey) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

}
