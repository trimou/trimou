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

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * Contextual instance holder.
 *
 * @author Martin Kouba
 */
final class ContextualInstance<T> {

	private final T instance;

	private final CreationalContext<T> creationalContext;

	private final Contextual<T> contextual;

	/**
	 *
	 * @param instance
	 * @param creationalContext
	 * @param contextual
	 */
	ContextualInstance(T instance, CreationalContext<T> creationalContext,
			Contextual<T> contextual) {
		super();
		this.instance = instance;
		this.creationalContext = creationalContext;
		this.contextual = contextual;
	}

	/**
	 *
	 * @return the instance
	 */
	public T getInstance() {
		return instance;
	}

	/**
	 *
	 * @return the creational context
	 */
	public CreationalContext<T> getCreationalContext() {
		return creationalContext;
	}

	/**
	 *
	 * @return the contextual (aka bean)
	 */
	public Contextual<T> getContextual() {
		return contextual;
	}

	/**
	 * Destroy the contextual instance properly.
	 */
	public void destroy() {
		contextual.destroy(instance, creationalContext);
	}

}
