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

import static org.trimou.engine.config.EngineConfigurationKey.DEBUG_MODE;
import static org.trimou.engine.config.EngineConfigurationKey.REMOVE_STANDALONE_LINES;
import static org.trimou.engine.config.EngineConfigurationKey.REMOVE_UNNECESSARY_SEGMENTS;

import java.util.List;

import org.trimou.Mustache;
import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ExecutionContext.TargetStack;
import org.trimou.engine.context.ExecutionContextBuilder;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.listener.MustacheRenderingEvent;
import org.trimou.engine.resource.AbstractReleaseCallbackContainer;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.Lists;

/**
 * Template segment (aka Mustache template).
 *
 * @author Martin Kouba
 */
@Internal
public class TemplateSegment extends AbstractContainerSegment implements
        Mustache {

    private final MustacheEngine engine;

    private boolean readOnly = false;

    private boolean debugMode;

    public TemplateSegment(String text, MustacheEngine engine) {
        super(text, null);
        this.engine = engine;
        this.debugMode = engine.getConfiguration().getBooleanPropertyValue(
                DEBUG_MODE);
    }

    @Override
    public void render(Appendable appendable, Object data) {

        checkIsReady();
        DefaultMustacheRenderingEvent event = new DefaultMustacheRenderingEvent(
                getText());

        try {
            renderingStarted(event);
            // Build execution context and push the template on the invocation
            // stack
            ExecutionContext context = new ExecutionContextBuilder(engine)
                    .withData(data).build(debugMode);
            context.push(TargetStack.TEMPLATE_INVOCATION, this);
            // Execute the template
            super.execute(appendable, context);
            renderingFinished(event);
        } finally {
            event.release();
        }
    }

    @Override
    public String render(Object data) {
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
        return String.format("%s: [%s]", getType(), getName());
    }

    protected MustacheEngine getEngine() {
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

    private void checkIsReady() {
        if (!isReadOnly()) {
            throw new MustacheException(MustacheProblem.TEMPLATE_NOT_READY,
                    "Template %s is not ready yet", getName());
        }
    }

}
