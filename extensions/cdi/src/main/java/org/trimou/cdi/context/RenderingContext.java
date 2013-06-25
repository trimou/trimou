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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.listener.MustacheRenderingEvent;

/**
 * Context for {@link RenderingScoped}.
 *
 * @author Martin Kouba
 */
public final class RenderingContext implements Context {

	/**
	 * Singleton
	 */
	public static final RenderingContext INSTANCE = new RenderingContext();

	private static final Logger logger = LoggerFactory
			.getLogger(RenderingContext.class);

	private final ThreadLocal<Map<Integer, ContextualInstance<?>>> contextualInstancesMap = new ThreadLocal<Map<Integer, ContextualInstance<?>>>();

	private final ConcurrentHashMap<Contextual<?>, Integer> contextualsMap = new ConcurrentHashMap<Contextual<?>, Integer>();

	private final AtomicInteger idSequence = new AtomicInteger();

	private RenderingContext() {
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return RenderingScoped.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Contextual<T> contextual,
			CreationalContext<T> creationalContext) {

		checkArgumentNotNull(contextual);

		if (!isActive()) {
			throw new ContextNotActiveException();
		}

		Map<Integer, ContextualInstance<?>> instancesMap = contextualInstancesMap
				.get();
		Integer id = getId(contextual);
		ContextualInstance<T> contextualInstance = (ContextualInstance<T>) instancesMap
				.get(id);

		if (contextualInstance != null) {
			return contextualInstance.getInstance();
		} else if (creationalContext != null) {
			T instance = contextual.create(creationalContext);
			instancesMap.put(id, new ContextualInstance<T>(instance,
					creationalContext, contextual));
			return instance;
		}
		return null;
	}

	@Override
	public <T> T get(Contextual<T> contextual) {
		return get(contextual, null);
	}

	@Override
	public boolean isActive() {
		return contextualInstancesMap.get() != null;
	}

	private Integer getId(Contextual<?> contextual) {

		Integer id = contextualsMap.get(contextual);

		if (id == null) {
			synchronized (contextual) {
				id = idSequence.incrementAndGet();
				contextualsMap.put(contextual, id);
			}
		}
		return id;
	}

	void initialize(MustacheRenderingEvent event) {
		logger.debug("Rendering started - init context [mustache: {}]",
				event.getMustacheName());
		contextualInstancesMap
				.set(new HashMap<Integer, ContextualInstance<?>>());
	}

	void destroy(MustacheRenderingEvent event) {

		Map<Integer, ContextualInstance<?>> contextualInstances = contextualInstancesMap
				.get();

		if (contextualInstances == null) {
			logger.warn("Cannot destroy context - contextual instances map is null");
			return;
		}

		logger.debug("Rendering finished - destroy context [mustache: {}]",
				event.getMustacheName());

		try {
			for (ContextualInstance<?> contextualInstance : contextualInstances
					.values()) {
				logger.trace("Destroying contextual instance [contextual: {}]",
						contextualInstance.getContextual());
				contextualInstance.destroy();
			}
			contextualInstances.clear();
		} finally {
			contextualInstancesMap.remove();
		}
	}

}
