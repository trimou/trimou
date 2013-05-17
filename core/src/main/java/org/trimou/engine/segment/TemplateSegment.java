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

import static org.trimou.engine.config.EngineConfigurationKey.DEBUG_MODE_ENABLED;

import java.util.HashMap;
import java.util.Map;

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.context.DebugExecutionContext;
import org.trimou.engine.context.DefaultExecutionContext;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Template segment.
 *
 * @author Martin Kouba
 */
public class TemplateSegment extends ContainerSegment implements Mustache {

	private final MustacheEngine engine;

	private boolean readOnly = false;

	public TemplateSegment(String text, MustacheEngine engine) {
		super(text, null);
		this.engine = engine;
	}

	@Override
	public void render(Appendable appendable, Map<String, Object> data) {
		if (!isReadOnly()) {
			throw new MustacheException(MustacheProblem.TEMPLATE_NOT_READY);
		}
		super.execute(appendable, newExecutionContext(data));
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
	public String getName() {
		return getText();
	}

	/**
	 * Make the template read only.
	 */
	public void setReadOnly() {
		this.readOnly = true;
	}

	/**
	 * @return <code>true</code> if read only, <code>false</code> otherwise
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	protected MustacheEngine getEngine() {
		return engine;
	}

	private ExecutionContext newExecutionContext(Map<String, Object> data) {

		ExecutionContext ctx = null;

		if (engine.getConfiguration().getBooleanPropertyValue(
				DEBUG_MODE_ENABLED)) {
			ctx = new DebugExecutionContext(engine.getConfiguration()
					.getResolvers());
		} else {
			ctx = new DefaultExecutionContext(engine.getConfiguration()
					.getResolvers());
		}

		Map<String, Object> contextData = new HashMap<String, Object>();

		if (engine.getConfiguration().getGlobalData() != null) {
			contextData.putAll(engine.getConfiguration().getGlobalData());
		}
		if (data != null) {
			contextData.putAll(data);
		}
		if (!contextData.isEmpty()) {
			ctx.push(contextData);
		}
		return ctx;
	}

}
