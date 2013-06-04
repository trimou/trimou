package org.trimou.engine.context;

import static org.trimou.engine.config.EngineConfigurationKey.DEBUG_MODE_ENABLED;

import java.util.HashMap;
import java.util.Map;

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.context.ExecutionContext.TargetStack;

/**
 *
 * @author Martin Kouba
 */
@Internal
public class ExecutionContextBuilder {

	private final MustacheEngine engine;

	private Map<String, Object> data;

	/**
	 *
	 * @param engine
	 */
	public ExecutionContextBuilder(MustacheEngine engine) {
		this.engine = engine;
	}

	/**
	 *
	 * @param data
	 * @return self
	 */
	public ExecutionContextBuilder withData(Map<String, Object> data) {
		this.data = data;
		return this;
	}

	/**
	 *
	 * @return the built execution context
	 */
	public ExecutionContext build() {

		ExecutionContext context = null;

		if (engine.getConfiguration().getBooleanPropertyValue(
				DEBUG_MODE_ENABLED)) {
			context = new DebugExecutionContext(engine.getConfiguration());
		} else {
			context = new DefaultExecutionContext(engine.getConfiguration());
		}

		Map<String, Object> contextData = new HashMap<String, Object>();

		if (engine.getConfiguration().getGlobalData() != null) {
			contextData.putAll(engine.getConfiguration().getGlobalData());
		}
		if (data != null) {
			contextData.putAll(data);
		}
		if (!contextData.isEmpty()) {
			context.push(TargetStack.CONTEXT, contextData);
		}
		return context;
	}

}
