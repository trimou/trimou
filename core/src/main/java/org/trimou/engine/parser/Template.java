/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.engine.parser;

import static org.trimou.engine.config.EngineConfigurationKey.DEBUG_MODE;

import java.util.List;

import org.trimou.Mustache;
import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.context.ExecutionContextBuilder;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.listener.MustacheRenderingEvent;
import org.trimou.engine.resource.AbstractReleaseCallbackContainer;
import org.trimou.engine.segment.RootSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.Lists;

/**
 * Mustache template.
 *
 * @author Martin Kouba
 */
@Internal
public class Template implements Mustache {

    private final String name;

    private final MustacheEngine engine;

    private final boolean debugMode;

    private RootSegment rootSegment;

    /**
     *
     * @param name
     * @param engine
     */
    public Template(String name, MustacheEngine engine) {
        this.name = name;
        this.engine = engine;
        this.debugMode = engine.getConfiguration().getBooleanPropertyValue(
                DEBUG_MODE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String render(Object data) {
        StringBuilder builder = new StringBuilder();
        render(builder, data);
        return builder.toString();
    }

    @Override
    public void render(Appendable appendable, Object data) {

        DefaultMustacheRenderingEvent event = new DefaultMustacheRenderingEvent(
                name);

        try {
            renderingStarted(event);

            // Build the execution context and execute the root segment
            rootSegment.execute(appendable, new ExecutionContextBuilder(engine)
                    .withData(data).build(debugMode));

            renderingFinished(event);
        } finally {
            event.release();
        }
    }

    public RootSegment getRootSegment() {
        return rootSegment;
    }

    public void setRootSegment(RootSegment rootSegment) {
        if (this.rootSegment != null) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED);
        }
        this.rootSegment = rootSegment;
    }

    public MustacheEngine getEngine() {
        return engine;
    }

    private void renderingStarted(MustacheRenderingEvent event) {
        List<MustacheListener> listeners = engine.getConfiguration()
                .getMustacheListeners();
        if (listeners != null) {
            for (MustacheListener listener : listeners) {
                listener.renderingStarted(event);
            }
        }
    }

    private void renderingFinished(MustacheRenderingEvent event) {
        List<MustacheListener> listeners = engine.getConfiguration()
                .getMustacheListeners();
        if (listeners != null) {
            for (MustacheListener listener : Lists.reverse(listeners)) {
                listener.renderingFinished(event);
            }
        }
    }

    /**
     *
     * @author Martin Kouba
     */
    private static final class DefaultMustacheRenderingEvent extends
            AbstractReleaseCallbackContainer implements MustacheRenderingEvent {

        private final String mustacheName;

        /**
         *
         * @param mustacheName
         */
        public DefaultMustacheRenderingEvent(String mustacheName) {
            super();
            this.mustacheName = mustacheName;
        }

        public String getMustacheName() {
            return mustacheName;
        }

    }

}
