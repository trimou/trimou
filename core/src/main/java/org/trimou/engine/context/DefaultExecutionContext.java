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
package org.trimou.engine.context;

import java.util.List;

import org.trimou.spi.engine.Resolver;

/**
 *
 * @author Martin Kouba
 */
public class DefaultExecutionContext extends AbstractExecutionContext {

	public DefaultExecutionContext(List<Resolver> resolvers) {
		super(resolvers);
	}

	@Override
	public Object get(String key, String segmentId) {

		Object value = null;

		if (isCompoundKey(key)) {

			String[] parts = splitKey(key);

			// Resolve the leading context object
			value = resolveLeadingContextObject(parts[0]);
			if (value == null) {
				// Not found - miss
				return null;
			}

			for (int i = 1; i < parts.length; i++) {
				value = resolve(value, parts[i]);
				if (value == null) {
					// Not found - miss
					break;
				}
			}
		} else {
			value = resolveLeadingContextObject(key);
		}
		return value;
	}

}
