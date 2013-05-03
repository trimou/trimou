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

import static org.trimou.util.Priorities.after;

import java.util.List;

/**
 * Resolve index-based access to lists.
 *
 * E.g. get the first element of <code>myList</code>:
 *
 * <pre>
 * {{myList.0}}
 * </pre>
 */
public class ListIndexResolver extends IndexResolver {

	public static final int LIST_RESOLVER_PRIORITY = after(MapResolver.MAP_RESOLVER_PRIORITY);

	@SuppressWarnings("rawtypes")
	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null || notAnIndex(name)
				|| !(contextObject instanceof List)) {
			return null;
		}

		List list = (List) contextObject;
		Integer index = getIndexValue(name, list.size());

		if (index != null) {
			return list.get(index);
		}
		return null;
	}

	@Override
	public int getPriority() {
		return LIST_RESOLVER_PRIORITY;
	}

}
