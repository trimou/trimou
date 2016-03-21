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

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.trimou.Mustache;
import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ExecutionContexts;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.listener.MustacheRenderingEvent;
import org.trimou.engine.resource.AbstractReleaseCallbackContainer;
import org.trimou.engine.segment.RootSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableMap.ImmutableMapBuilder;

/**
 * A Mustache template.
 *
 * @author Martin Kouba
 */
@Internal
public class Template implements Mustache {

    private final long generatedId;

    private final String name;

    private final MustacheEngine engine;

    private final ExecutionContext globalExecutionContext;

    private final Map<String, Template> nestedTemplates;

    private volatile Template parent;

    private volatile RootSegment rootSegment;

    /**
     *
     * @param generatedId
     * @param name
     * @param engine
     */
    public Template(Long generatedId, String name, MustacheEngine engine) {
        this(generatedId, name, engine, null);
    }

    /**
     *
     * @param generatedId
     * @param name
     * @param engine
     * @param nestedTemplates
     */
    public Template(Long generatedId, String name, MustacheEngine engine,
            List<Template> nestedTemplates) {
        this(generatedId, name, engine, nestedTemplates, null);
    }

   /**
     *
     * @param generatedId
     * @param name
     * @param engine
     * @param nestedTemplates
     * @param parent
     */
    private Template(Long generatedId, String name, MustacheEngine engine,
            List<Template> nestedTemplates, Template parent) {
        this.generatedId = generatedId;
        this.name = name;
        this.engine = engine;
        this.globalExecutionContext = ExecutionContexts
                .newGlobalExecutionContext(engine.getConfiguration());
        if (nestedTemplates == null || nestedTemplates.isEmpty()) {
            this.nestedTemplates = Collections.emptyMap();
        } else {
            ImmutableMapBuilder<String, Template> builder = ImmutableMap
                    .builder();
            for (Template template : nestedTemplates) {
                builder.put(template.getName(), template);
            }
            this.nestedTemplates = builder.build();
        }
        this.parent = parent;
    }

    @Override
    public Long getGeneratedId() {
        return generatedId;
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
        final DefaultMustacheRenderingEvent event = new DefaultMustacheRenderingEvent(
                name, generatedId, engine.getConfiguration()
                        .getIdentifierGenerator()
                        .generate(MustacheRenderingEvent.class));
        try {
            renderingStarted(event);
            appendable = rootSegment.execute(
                    appendable,
                    data != null ? globalExecutionContext
                            .setContextObject(data) : globalExecutionContext);
            // We need for flush the async appendable if needed
            RootSegment.flushAsyncAppendable(appendable);
            renderingFinished(event);
        } finally {
            event.release();
        }
    }

    public RootSegment getRootSegment() {
        return rootSegment;
    }

    synchronized void setRootSegment(RootSegment rootSegment) {
        if (this.rootSegment != null) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED);
        }
        this.rootSegment = rootSegment;
    }

    synchronized void setParent(Template parent) {
        if (this.parent != null) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED);
        }
        this.parent = parent;
    }

    public MustacheEngine getEngine() {
        return engine;
    }

    public Template getNestedTemplate(String name) {
        return parent != null ? parent.getNestedTemplate(name) : nestedTemplates.get(name);
    }

    private void renderingStarted(MustacheRenderingEvent event) {
        List<MustacheListener> listeners = engine.getConfiguration()
                .getMustacheListeners();
        if (listeners.isEmpty()) {
            return;
        }
        for (MustacheListener listener : listeners) {
            listener.renderingStarted(event);
        }
    }

    private void renderingFinished(MustacheRenderingEvent event) {
        List<MustacheListener> listeners = engine.getConfiguration()
                .getMustacheListeners();
        if (listeners.isEmpty()) {
            return;
        }
        for (ListIterator<MustacheListener> iterator = listeners
                .listIterator(listeners.size()); iterator.hasPrevious();) {
            iterator.previous().renderingFinished(event);
        }
    }

    /**
     *
     * @author Martin Kouba
     */
    private static final class DefaultMustacheRenderingEvent extends
            AbstractReleaseCallbackContainer implements MustacheRenderingEvent {

        private final String mustacheName;

        private final long mustacheId;

        private final long generatedId;

        /**
         *
         * @param mustacheName
         * @param sequenceValue
         */
        public DefaultMustacheRenderingEvent(String mustacheName,
                long mustacheId, long sequenceValue) {
            this.mustacheName = mustacheName;
            this.mustacheId = mustacheId;
            this.generatedId = sequenceValue;
        }

        @Override
        public String getMustacheName() {
            return mustacheName;
        }

        @Override
        public long getMustacheGeneratedId() {
            return mustacheId;
        }

        @Override
        public Long getGeneratedId() {
            return generatedId;
        }

    }

    @Override
    public String toString() {
        return String.format("Template %s", name);
    }

}
