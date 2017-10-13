/*
 * Copyright 2017 Martin Kouba
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
package org.trimou.el;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.trimou.engine.config.ConfigurationKey;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.SetHelper;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableMap.ImmutableMapBuilder;

/**
 * Extends {@link SetHelper} in the sense that a {@link String} param is
 * evaluated as EL expression:
 *
 * <pre>
 * {{#set couple='["foo","bar"]'}}
 *   {{couple.0}}:{{couple.1}}
 * {{/set}}
 * </pre>
 *
 * @author Martin Kouba
 * @since 2.4
 */
public class ELSetHelper extends SetHelper {

    @Override
    protected Map<String, Object> getMap(Options options) {
        ImmutableMapBuilder<String, Object> builder = ImmutableMap.builder();
        for (Entry<String, Object> entry : options.getHash().entrySet()) {
            if (entry.getValue() instanceof String) {
                // String is expected to be an EL expression
                builder.put(entry.getKey(), Expressions.eval(entry.getValue().toString(), options, configuration));
            } else {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ELProcessorFactory.EL_PROCESSOR_FACTORY_KEY);
    }

}
