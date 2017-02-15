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
package org.trimou.cdi.context;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps contextuals to instances for a certain thread.
 *
 * @author Martin Kouba
 */
final class ContextualInstanceStore {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ContextualInstanceStore.class);

    private final Map<Contextual<?>, ContextualInstance<?>> contextualInstancesMap;

    ContextualInstanceStore() {
        // Synchronization is not needed
        contextualInstancesMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    <T> ContextualInstance<T> get(Contextual<T> contextual,
            CreationalContext<T> creationalContext) {

        ContextualInstance<T> contextualInstance = (ContextualInstance<T>) contextualInstancesMap
                .get(contextual);

        if (contextualInstance == null && creationalContext != null) {
            contextualInstance = new ContextualInstance<>(
                    contextual.create(creationalContext), creationalContext,
                    contextual);
            contextualInstancesMap.put(contextual, contextualInstance);
        }
        return contextualInstance;
    }

    void destroy() {
        for (ContextualInstance<?> contextualInstance : contextualInstancesMap
                .values()) {
            LOGGER.trace("Destroying contextual instance [contextual: {}]",
                    contextualInstance.getContextual());
            contextualInstance.destroy();
        }
        contextualInstancesMap.clear();
    }

}
