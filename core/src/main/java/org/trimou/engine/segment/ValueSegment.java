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

import static org.trimou.engine.config.EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM;

import java.io.StringWriter;

import org.trimou.annotations.Internal;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.lambda.Lambda;

/**
 * Value rendering segment.
 *
 * @author Martin Kouba
 */
@Internal
public class ValueSegment extends AbstractSegment {

	private final boolean unescape;

	public ValueSegment(String text, Origin origin, boolean unescape) {
		super(text, origin);
		this.unescape = unescape;
	}

	public SegmentType getType() {
		return SegmentType.VALUE;
	}

	public boolean isUnescape() {
		return unescape;
	}

	public void execute(Appendable appendable, ExecutionContext context) {

		Object value = context.getValue(getText());

		if (value != null) {
			if (value instanceof Lambda) {
				processLambda(appendable, context, value);
			} else {
				writeValue(appendable, value.toString());
			}
		} else {
			// By default a variable miss returns an empty string
			if (getEngineConfiguration().getBooleanPropertyValue(
					NO_VALUE_INDICATES_PROBLEM)) {
				throw new MustacheException(
						MustacheProblem.RENDER_NO_VALUE,
						"No value for the given key found: %s %s",
						getText(), getOrigin());
			}
		}
	}

	@Override
	protected String getSegmentName() {
		return getText();
	}

	private void writeValue(Appendable appendable, String text) {
		append(appendable, unescape ? text : getEngineConfiguration()
				.getTextSupport().escapeHtml(text));
	}

	private void processLambda(Appendable appendable, ExecutionContext context,
			Object value) {

		Lambda lambda = (Lambda) value;

		String returnValue = lambda.invoke(null);

		if (lambda.isReturnValueInterpolated()) {
			// Parse and interpolate the return value
			StringWriter interpolated = new StringWriter();
			TemplateSegment temp = (TemplateSegment) getEngine()
					.compileMustache(
							lambda.getClass() + "_" + System.nanoTime(),
							returnValue);
			temp.execute(interpolated, context);
			writeValue(appendable, interpolated.toString());
		} else {
			writeValue(appendable, returnValue);
		}
	}

}
