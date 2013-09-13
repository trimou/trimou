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

import org.trimou.engine.config.Configuration;

/**
 *
 * @author Martin Kouba
 */
class DefaultExecutionContext extends AbstractExecutionContext {

    public DefaultExecutionContext(Configuration configuration) {
        super(configuration);
    }

    @Override
    public ValueWrapper getValue(String key) {

        ValueWrapper value = new ValueWrapper();
        Object lastValue = null;
        Iterator<String> parts = keySplitter().split(key);

        lastValue = resolveLeadingContextObject(parts.next(), value);

        if (lastValue == null) {
            // Not found - miss
            return value;
        }

        while (parts.hasNext()) {
            lastValue = resolve(lastValue, parts.next(), value);
            if (lastValue == null) {
                // Not found - miss
                return value;
            }
        }
        value.set(lastValue);
        return value;
    }

}
