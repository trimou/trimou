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

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Iterator;

import org.trimou.engine.context.ExecutionContext;
import org.trimou.lambda.Lambda;

/**
 * Section segment.
 *
 * <p>
 * The content is not rendered if there is no object found in the context, or
 * the found object is:
 * </p>
 * <ul>
 * <li>a {@link Boolean} of value <code>false</code>,</li>
 * <li>an {@link Iterable} with no elements,</li>
 * <li>an empty array.</li>
 * </ul>
 *
 * <p>
 * The content is rendered one or more times if there is an object found in the
 * context. If the found object is:
 * </p>
 * <ul>
 * <li>non-empty {@link Iterable} or array, the content is rendered for each
 * element,</li>
 * <li>a {@link Boolean} of value <code>true</code>, the content is rendered
 * once,</li>
 * <li>an instance of {@link Lambda}, the content is processed according to the
 * lambda's specification,</li>
 * <li>any other kind of object represents a nested context.</li>
 * <ul>
 *
 * @author Martin Kouba
 * @see Lambda
 * @see InvertedSectionSegment
 */
public class SectionSegment extends AbstractSectionSegment {

	public SectionSegment(String text, TemplateSegment template) {
		super(text, template);
	}

	public SegmentType getType() {
		return SegmentType.SECTION;
	}

	public void execute(Appendable appendable, ExecutionContext context) {

		Object value = context.getValue(getText());

		if (value == null) {
			return;
		}

		if (value instanceof Boolean) {
			// Boolean#TRUE, true
			if ((Boolean) value) {
				super.execute(appendable, context);
			}
		} else if (value instanceof Iterable) {
			// Iterable
			processIterable(appendable, context, value);
		} else if (value.getClass().isArray()) {
			// Array
			processArray(appendable, context, value);
			context.pop();
		} else if (value instanceof Lambda) {
			// Lambda
			processLambda(appendable, context, value);
		} else {
			// Nested context
			context.push(value);
			super.execute(appendable, context);
			context.pop();
		}
	}

	@SuppressWarnings("rawtypes")
	private void processIterable(Appendable appendable, ExecutionContext context,
			Object value) {

		Iterator iterator = ((Iterable) value).iterator();
		IterationMeta meta = new IterationMeta(iterator);
		context.push(meta);
		while (iterator.hasNext()) {
			context.push(iterator.next());
			super.execute(appendable, context);
			context.pop();
			meta.nextIteration();
		}
		context.pop();
	}

	private void processArray(Appendable appendable, ExecutionContext context,
			Object value) {

		int length = Array.getLength(value);
		IterationMeta meta = new IterationMeta(length);
		// Push iteration meta
		context.push(meta);
		for (int i = 0; i < length; i++) {
			context.push(Array.get(value, i));
			super.execute(appendable, context);
			context.pop();
			meta.nextIteration();
		}
		// Pop iteration meta
		context.pop();
	}

	private void processLambda(Appendable appendable, ExecutionContext context,
			Object value) {

		Lambda lambda = (Lambda) value;

		String input;
		switch (lambda.getInputType()) {
		case LITERAL:
			// Try to reconstruct the original text
			input = getContainingLiteralBlock();
			break;
		case PROCESSED:
			StringWriter processed = new StringWriter();
			super.execute(processed, context);
			input = processed.toString();
			break;
		default:
			throw new UnsupportedOperationException();
		}

		// Invoke lambda
		String returnValue = lambda.invoke(input);

		if (lambda.isReturnValueInterpolated()) {
			// Parse and interpolate the return value
			TemplateSegment temp = (TemplateSegment) getEngine().compileMustache(
					getTemplate().getName() + "_" + System.nanoTime(),
					returnValue);
			temp.execute(appendable, context);
		} else {
			append(appendable, returnValue);
		}
	}

}
