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
package org.trimou.engine.resolver;

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.priority.WithPriority;

/**
 * Context object resolver. Implementation must be thread-safe.
 *
 * @author Martin Kouba
 */
public interface Resolver extends WithPriority, ConfigurationAware {

	/**
	 * Resolve the value from specified context object and name. This method
	 * should return as fast as possible. The best practice is to verify params
	 * first and return <code>null</code> in case of the resolver is not capable
	 * of resolving it.
	 *
	 * @param contextObject
	 *            The context object may be <code>null</code>
	 * @param name
	 *            The name (part of the key) is never <code>null</code>
	 * @return the resolved object or <code>null</code>
	 */
	public Object resolve(Object contextObject, String name);

}
