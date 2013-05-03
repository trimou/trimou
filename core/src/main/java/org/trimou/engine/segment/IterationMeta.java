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
package org.trimou.engine.segment;

import java.util.Iterator;

/**
 * Iteration metadata.
 *
 * @author Martin Kouba
 */
public final class IterationMeta {

	private final Iterator<?> iterator;

	private final int length;

	private int index;

	IterationMeta(Iterator<?> iterator) {
		super();
		this.iterator = iterator;
		this.index = 0;
		this.length = 0;
	}

	IterationMeta(int length) {
		super();
		this.iterator = null;
		this.index = 0;
		this.length = length;
	}

	/**
	 *
	 * @return
	 */
	public int getIterIndex() {
		return index;
	}

	/**
	 *
	 * @return
	 */
	public boolean getIterHasNext() {
		return iterator != null ? iterator.hasNext() : (index < (length - 1));
	}

	void nextIteration() {
		index++;
	}

}
