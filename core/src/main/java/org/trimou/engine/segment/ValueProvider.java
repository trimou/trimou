/*
 * Copyright 2015 Martin Kouba
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
package org.trimou.engine.segment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ValueWrapper;
import org.trimou.engine.resolver.EnhancedResolver.Hint;

/**
 *
 * @author Martin Kouba
 */
class ValueProvider {

    private final String key;

    private final String[] keyParts;

    /**
     * The hint is currently used to skip the resolver chain for the first
     * part of the key.
     *
     * @see EngineConfigurationKey#RESOLVER_HINTS_ENABLED
     */
    private final AtomicReference<Hint> hint;

    /**
     *
     * @param text
     * @param configuration
     */
    ValueProvider(String text, Configuration configuration) {
        this.key = text;
        ArrayList<String> parts = new ArrayList<>();
        for (Iterator<String> iterator = configuration.getKeySplitter()
                .split(text); iterator.hasNext();) {
            parts.add(iterator.next());
        }
        this.keyParts = parts.toArray(new String[parts.size()]);
        if (configuration.getBooleanPropertyValue(
                EngineConfigurationKey.RESOLVER_HINTS_ENABLED)) {
            this.hint = new AtomicReference<>();
        } else {
            this.hint = null;
        }
    }

    ValueWrapper get(ExecutionContext context) {
        ValueWrapper value = context.getValue(key, keyParts, hint);
        if (hint != null && value.getHint() != null) {
            hint.compareAndSet(null, value.getHint());
        }
        return value;
    }

}
