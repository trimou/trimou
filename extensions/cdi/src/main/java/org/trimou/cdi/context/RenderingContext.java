/*
 * Copyright 2013 - 2017 Trimou team
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
import java.util.HashMap;
import java.util.Map;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(RenderingContext.class);

    private final ThreadLocal<Map<Contextual<?>, ContextualInstance<?>>> currentContext = new ThreadLocal<>();

    @Override
    public Class<? extends Annotation> getScope() {
        return RenderingScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {

        checkArgumentNotNull(contextual);
        Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();

        if (ctx == null) {
            throw new ContextNotActiveException();
        }

        @SuppressWarnings("unchecked")
        ContextualInstance<T> instance = (ContextualInstance<T>) ctx.get(contextual);

        if (instance == null && creationalContext != null) {
            instance = new ContextualInstance<T>(contextual.create(creationalContext), creationalContext, contextual);
            ctx.put(contextual, instance);
        }

        return instance != null ? instance.get() : null;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public boolean isActive() {
        return currentContext.get() != null;
    }

    void initialize(MustacheRenderingEvent event) {
        LOGGER.debug("Rendering started - init context [template: {}]", event.getMustacheName());
        currentContext.set(new HashMap<>());
    }

    void destroy(MustacheRenderingEvent event) {
        Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();
        if (ctx == null) {
            LOGGER.warn("Cannot destroy context - current context is null");
            return;
        }
        LOGGER.debug("Rendering finished - destroy context [template: {}]", event.getMustacheName());
        for (ContextualInstance<?> instance : ctx.values()) {
            try {
                LOGGER.trace("Destroying contextual instance [contextual: {}]", instance.getContextual());
                instance.destroy();
            } catch (Exception e) {
                LOGGER.warn("Unable to destroy instance" + instance.get() + " for bean: " + instance.getContextual());
            }
        }
        ctx.clear();
        currentContext.remove();
    }

}
