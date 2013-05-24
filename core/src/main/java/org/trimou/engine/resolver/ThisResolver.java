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

import org.trimou.engine.priority.WithPriority;
import org.trimou.util.Strings;

/**
 * @author Martin Kouba
 */
public class ThisResolver extends AbstractResolver {

	public static final int THIS_RESOLVER_PRIORITY = after(WithPriority.BUILTIN_RESOLVERS_DEFAULT_PRIORITY);

	public static final String NAME_THIS = "this";

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null) {
			return null;
		}

		if (NAME_THIS.equals(name) || Strings.KEY_SEPARATOR.equals(name)) {
			return contextObject;
		}
		return null;
	}

	@Override
	public int getPriority() {
		return THIS_RESOLVER_PRIORITY;
	}

}