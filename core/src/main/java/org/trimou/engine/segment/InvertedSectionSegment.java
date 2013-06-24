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

import java.lang.reflect.Array;
import java.util.Iterator;

import org.trimou.annotations.Internal;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ValueWrapper;

/**
 * Inverted section segment.
 *
 * <p>
 * The content is rendered if there is no object found in the context, or is a
 * {@link Boolean} of value <code>false</code>, or is an {@link Iterable} with
 * no elements, or is an empty array.
 * </p>
 *
 * @author Martin Kouba
 */
@Internal
public class InvertedSectionSegment extends AbstractSectionSegment {

	public InvertedSectionSegment(String text, Origin origin) {
		super(text, origin);
	}

	public SegmentType getType() {
		return SegmentType.INVERTED_SECTION;
	}

	public void execute(Appendable appendable, ExecutionContext context) {

		ValueWrapper value = context.getValue(getText());

		try {
			if (value.isNull()
					|| processValue(appendable, context, value.get())) {
				super.execute(appendable, context);
			}
		} finally {
			value.release();
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean processValue(Appendable appendable,
			ExecutionContext context, Object value) {
		if (value instanceof Boolean) {
			// Boolean
			if (!(Boolean) value) {
				return true;
			}
		} else if (value instanceof Iterable) {
			// No elements to iterate
			Iterator iterator = ((Iterable) value).iterator();
			if (!iterator.hasNext()) {
				return true;
			}
		} else if (value.getClass().isArray()) {
			// Array is empty
			if (Array.getLength(value) == 0) {
				return true;
			}
		}
		return false;
	}

}
