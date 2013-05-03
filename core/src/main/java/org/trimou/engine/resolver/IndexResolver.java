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

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Abstract index-based resolver.
 *
 * @author Martin Kouba
 * @see ListIndexResolver
 * @see ArrayIndexResolver
 */
public abstract class IndexResolver extends AbstractResolver {

	/**
	 * @param name
	 * @return <code>true</code> if the given key doesn't represent an index
	 *         (must only contain digits)
	 */
	protected boolean notAnIndex(String name) {
		return !NumberUtils.isDigits(name);
	}

	/**
	 *
	 * @param key
	 * @param maxSize
	 * @return the index value or <code>null</code> in case of invalid index
	 */
	protected Integer getIndexValue(String key, int maxSize) {

		Integer index = null;

		try {
			index = Integer.valueOf(key);
		} catch (NumberFormatException e) {
			// Index is not an integer
			return null;
		}

		if (index < 0 || index >= maxSize) {
			// Index out of bound
			return null;
		}
		return index;
	}

}
