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

import static org.trimou.engine.config.EngineConfigurationKey.DEBUG_MODE;

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

        if (engine.getConfiguration().getBooleanPropertyValue(DEBUG_MODE)) {
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
