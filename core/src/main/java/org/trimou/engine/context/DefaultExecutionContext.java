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
package org.trimou.engine.context;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.resolver.EnhancedResolver.Hint;

/**
 *
 * @author Martin Kouba
 */
class DefaultExecutionContext extends AbstractExecutionContext {

    public DefaultExecutionContext(Configuration configuration) {
        super(configuration);
    }

    @Override
    public ValueWrapper getValue(String key, String[] keyParts, AtomicReference<Hint> hintRef) {

        ValueWrapper value = new ValueWrapper(key);
        Object lastValue = null;

        if (keyParts == null || keyParts.length == 0) {
            Iterator<String> parts = configuration.getKeySplitter().split(key);
            lastValue = resolveLeadingContextObject(parts.next(), value, hintRef);
            if (lastValue == null) {
                // Leading context object not found - miss
                return value;
            }
            while (parts.hasNext()) {
                value.processNextPart();
                lastValue = resolve(lastValue, parts.next(), value, false);
                if (lastValue == null) {
                    // Not found - miss
                    return value;
                }
            }
        } else {
            lastValue = resolveLeadingContextObject(keyParts[0], value, hintRef);
            if (lastValue == null) {
                // Leading context object not found - miss
                return value;
            }
            if (keyParts.length > 1) {
                for (int i = 1; i < keyParts.length; i++) {
                    value.processNextPart();
                    lastValue = resolve(lastValue, keyParts[i], value, false);
                    if (lastValue == null) {
                        // Not found - miss
                        return value;
                    }
                }
            }
        }
        value.set(lastValue);
        return value;
    }

    @Override
    public ValueWrapper getValue(String key) {
        return getValue(key, null, null);
    }

}
