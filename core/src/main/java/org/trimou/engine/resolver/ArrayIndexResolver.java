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

import static org.trimou.engine.priority.Priorities.after;

import java.lang.reflect.Array;

/**
 * Resolve index-based access to arrays.
 *
 * E.g. get the first element of <code>myArray</code>:
 *
 * <pre>
 * {{myArray.0}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class ArrayIndexResolver extends IndexResolver {

	public static final int ARRAY_RESOLVER_PRIORITY = after(ListIndexResolver.LIST_RESOLVER_PRIORITY);

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null || notAnIndex(name)
				|| !isArray(contextObject)) {
			return null;
		}

		Integer index = getIndexValue(name, Array.getLength(contextObject));

		if (index != null) {
			return Array.get(contextObject, index);
		}
		return null;
	}

	private boolean isArray(Object base) {

		if (base.getClass().isArray()) {
			return true;
		}
		return false;
	}

	@Override
	public int getPriority() {
		return ARRAY_RESOLVER_PRIORITY;
	}

}
