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

import static org.trimou.engine.config.EngineConfigurationKey.REMOVE_STANDALONE_LINES;
import static org.trimou.engine.config.EngineConfigurationKey.REMOVE_UNNECESSARY_SEGMENTS;

import java.util.Map;

import org.trimou.Mustache;
import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ExecutionContext.TargetStack;
import org.trimou.engine.context.ExecutionContextBuilder;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Template segment.
 *
 * @author Martin Kouba
 */
@Internal
public class TemplateSegment extends AbstractContainerSegment implements
		Mustache {

	private final MustacheEngine engine;

	private boolean readOnly = false;

	public TemplateSegment(String text, MustacheEngine engine) {
		super(text, null);
		this.engine = engine;
	}

	@Override
	public void render(Appendable appendable, Map<String, Object> data) {
		if (!isReadOnly()) {
			throw new MustacheException(MustacheProblem.TEMPLATE_NOT_READY,
					"Template %s is not ready", getName());
		}
		ExecutionContext context = new ExecutionContextBuilder(engine)
				.withData(data).build();
		context.push(TargetStack.TEMPLATE_INVOCATION, this);
		super.execute(appendable, context);
	}

	@Override
	public String render(Map<String, Object> data) {
		StringBuilder builder = new StringBuilder();
		render(builder, data);
		return builder.toString();
	}

	@Override
	public SegmentType getType() {
		return SegmentType.TEMPLATE;
	}

	@Override
	public String getLiteralBlock() {
		return getContainingLiteralBlock();
	}

	@Override
	public String getName() {
		return getText();
	}

	@Override
	public void performPostProcessing() {

		if (engine.getConfiguration().getBooleanPropertyValue(
				REMOVE_STANDALONE_LINES)) {
			Segments.removeStandaloneLines(this);
		}
		if (engine.getConfiguration().getBooleanPropertyValue(
				REMOVE_UNNECESSARY_SEGMENTS)) {
			Segments.removeUnnecessarySegments(this);
		}
		super.performPostProcessing();
		readOnly = true;
	}

	/**
	 * @return <code>true</code> if read only, <code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public String toString() {
		return String.format("%s: %s]", getType(), getName());
	}

	protected MustacheEngine getEngine() {
		return engine;
	}

}
