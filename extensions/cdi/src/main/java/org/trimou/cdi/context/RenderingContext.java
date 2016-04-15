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

import static org.trimou.util.Checker.checkArgumentNotNull;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.listener.MustacheRenderingEvent;

/**
 * A context for {@link RenderingScoped}.
 *
 * @author Martin Kouba
 */
public final class RenderingContext implements Context {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RenderingContext.class);

    private final ThreadLocal<ContextualInstanceStore> contextualInstanceStore = new ThreadLocal<ContextualInstanceStore>();

    @Override
    public Class<? extends Annotation> getScope() {
        return RenderingScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual,
            CreationalContext<T> creationalContext) {

        checkArgumentNotNull(contextual);

        ContextualInstanceStore store = contextualInstanceStore.get();

        if (store == null) {
            throw new ContextNotActiveException();
        }

        ContextualInstance<T> contextualInstance = store.get(contextual,
                creationalContext);

        if (contextualInstance != null) {
            return contextualInstance.getInstance();
        }
        return null;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public boolean isActive() {
        return contextualInstanceStore.get() != null;
    }

    void initialize(MustacheRenderingEvent event) {
        LOGGER.debug("Rendering started - init context [template: {}]",
                event.getMustacheName());
        contextualInstanceStore.set(new ContextualInstanceStore());
    }

    void destroy(MustacheRenderingEvent event) {
        ContextualInstanceStore store = contextualInstanceStore.get();
        if (store == null) {
            LOGGER.warn("Cannot destroy context - contextual instance store is null");
            return;
        }
        LOGGER.debug("Rendering finished - destroy context [template: {}]",
                event.getMustacheName());
        try {
            store.destroy();
        } finally {
            contextualInstanceStore.remove();
        }
    }

}
