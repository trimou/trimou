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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ValueWrapper;
import org.trimou.engine.interpolation.LiteralSupport;
import org.trimou.engine.parser.Template;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Helper;
import org.trimou.handlebars.HelperDefinition;
import org.trimou.handlebars.HelperDefinition.ValuePlaceholder;
import org.trimou.handlebars.Options;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableList;
import org.trimou.util.ImmutableList.ImmutableListBuilder;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableMap.ImmutableMapBuilder;
import org.trimou.util.Strings;

/**
 * Wraps {@link Helper} instance and handles its execution (e.g. builds
 * {@link Options} instance).
 *
 * @author Martin Kouba
 * @see HelperAwareSegment
 */
class HelperExecutionHandler {

    private final Helper helper;

    private final OptionsBuilder optionsBuilder;

    /**
     *
     * @param helper
     * @param optionsBuilder
     */
    private HelperExecutionHandler(Helper helper,
            OptionsBuilder optionsBuilder) {
        this.helper = helper;
        this.optionsBuilder = optionsBuilder;
    }

    /**
     *
     * @param name
     * @param engine
     * @param segment
     * @return a handler for the given name or <code>null</code> if no such
     *         helper exists
     */
    static HelperExecutionHandler from(String name, MustacheEngine engine,
            HelperAwareSegment segment) {

        // Split the name and detect unterminated literals
        Iterator<String> parts = splitHelperName(name, segment);

        Helper helper = engine.getConfiguration().getHelpers()
                .get(parts.next());

        if (helper == null) {
            // No helper with the given name found
            return null;
        }

        ImmutableListBuilder<Object> params = ImmutableList.builder();
        ImmutableMapBuilder<String, Object> hash = ImmutableMap.builder();
        LiteralSupport literalSupport = engine.getConfiguration()
                .getLiteralSupport();

        while (parts.hasNext()) {
            // Next part is a param or a hash entry
            String part = parts.next();
            if (Strings.isListLiteral(part)) {
                params.add(new ListValuePlaceholder(part, engine,
                        literalSupport, segment));
            } else {
                int equalsPosition = getFirstDeterminingEqualsCharPosition(
                        part);
                if (equalsPosition != -1) {
                    String value = part.substring(equalsPosition + 1,
                            part.length());
                    if (value.isEmpty()) {
                        throw new MustacheException(
                                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                                "Missing second operand of equals in part %s",
                                part);
                    }
                    if (Strings.isListLiteral(value)) {
                        hash.put(part.substring(0, equalsPosition),
                                new ListValuePlaceholder(value, engine,
                                        literalSupport, segment));
                    } else {
                        hash.put(part.substring(0, equalsPosition),
                                getLiteralOrPlaceholder(value, engine, segment,
                                        literalSupport));
                    }
                } else {
                    params.add(getLiteralOrPlaceholder(part, engine, segment,
                            literalSupport));
                }
            }
        }

        OptionsBuilder optionsBuilder = new OptionsBuilder(params.build(),
                hash.build(), segment, engine);

        // Let the helper validate the tag definition
        helper.validate(optionsBuilder);

        return new HelperExecutionHandler(helper, optionsBuilder);
    }

    /**
     *
     * @param appendable
     * @param executionContext
     */
    Appendable execute(Appendable appendable,
            ExecutionContext executionContext) {
        final DefaultOptions options = optionsBuilder.build(appendable,
                executionContext);
        try {
            helper.execute(options);
            return options.getAppendable();
        } finally {
            options.release();
        }
    }

    /**
     * Extracts parts from an input string. This implementation is quite naive
     * and should be possibly rewritten. Note that we can't use a simple
     * splitter because of string literals may contain whitespace chars.
     *
     * @param name
     * @param segment
     * @return the parts of the helper name
     * @throws MustacheException
     *             If a compilation problem occurs
     */
    static Iterator<String> splitHelperName(String name, Segment segment) {

        // stringLiteral contains the character that opened the
        // literal (' or "). null if not in a literal
        Character stringLiteral = null;
        boolean arrayLiteral = false;
        boolean space = false;
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (character == ' ') {
                if (!space) {
                    if (stringLiteral == null && !arrayLiteral) {
                        if (buffer.length() > 0) {
                            parts.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        space = true;
                    } else {
                        buffer.append(character);
                    }
                }
            } else {
                if (!arrayLiteral
                        && Strings.isStringLiteralSeparator(character)) {
                    if (stringLiteral != null) {
                        if (character == stringLiteral.charValue()) {
                            stringLiteral = null;
                        }
                    } else {
                        stringLiteral = character;
                    }
                } else if (stringLiteral == null
                        && Strings.isListLiteralStart(character)) {
                    arrayLiteral = true;
                } else if (stringLiteral == null
                        && Strings.isListLiteralEnd(character)) {
                    arrayLiteral = false;
                }
                space = false;
                buffer.append(character);
            }
        }

        if (buffer.length() > 0) {
            if (stringLiteral != null || arrayLiteral) {
                throw new MustacheException(
                        MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                        "Unterminated string or array literal detected: %s",
                        segment);
            }
            parts.add(buffer.toString());
        }
        return parts.iterator();
    }

    /**
     *
     * @param part
     * @return the index of an equals char outside of any string literal,
     *         <code>-1</code> if no such char is found
     */
    static int getFirstDeterminingEqualsCharPosition(String part) {
        boolean stringLiteral = false;
        for (int i = 0; i < part.length(); i++) {
            if (Strings.isStringLiteralSeparator(part.charAt(i))) {
                if (i == 0) {
                    // The first char is a string literal separator
                    return -1;
                }
                stringLiteral = !stringLiteral;
            } else {
                if (!stringLiteral && part.charAt(i) == '=') {
                    return i;
                }
            }
        }
        return -1;
    }

    private static Object getLiteralOrPlaceholder(String value,
            MustacheEngine engine, HelperAwareSegment segment,
            LiteralSupport literalSupport) {
        Object literal = literalSupport.getLiteral(value, segment.getTagInfo());
        return literal != null ? literal
                : new DefaultValuePlaceholder(value, engine);
    }

    private static class OptionsBuilder implements HelperDefinition {

        private final List<Object> parameters;

        private final Map<String, Object> hash;

        private final HelperAwareSegment segment;

        private final MustacheEngine engine;

        // true if no placeholder found, also if params list is empty
        private final boolean isParamValuePlaceholderFound;

        // true if no placeholder found, also if hash map is empty
        private final boolean isHashValuePlaceholderFound;

        private OptionsBuilder(List<Object> parameters,
                Map<String, Object> hash, HelperAwareSegment segment,
                MustacheEngine engine) {
            this.parameters = parameters;
            this.hash = hash;
            this.segment = segment;
            this.engine = engine;
            this.isParamValuePlaceholderFound = initParamValuePlaceholderFound(
                    parameters);
            this.isHashValuePlaceholderFound = initHashValuePlaceholderFound(
                    hash);
        }

        @Override
        public MustacheTagInfo getTagInfo() {
            return segment.getTagInfo();
        }

        @Override
        public List<Object> getParameters() {
            return parameters;
        }

        @Override
        public Map<String, Object> getHash() {
            return hash;
        }

        @Override
        public String getContentLiteralBlock() {
            if (segment instanceof ContainerSegment) {
                return ((ContainerSegment) segment).getContentLiteralBlock();
            } else {
                return Strings.EMPTY;
            }
        }

        public DefaultOptions build(Appendable appendable,
                ExecutionContext executionContext) {
            List<ValueWrapper> valueWrappers = isParamValuePlaceholderFound
                    || isHashValuePlaceholderFound ? new LinkedList<>() : null;
            return new DefaultOptions(appendable, executionContext, segment,
                    getFinalParameters(executionContext, valueWrappers),
                    getFinalHash(executionContext, valueWrappers),
                    valueWrappers, engine, this);
        }

        private List<Object> getFinalParameters(
                ExecutionContext executionContext,
                List<ValueWrapper> valueWrappers) {
            if (isParamValuePlaceholderFound) {
                // At this point parameters list is never empty
                int size = parameters.size();
                switch (size) {
                case 1:
                    // Very often there will be only single param
                    return Collections
                            .singletonList(resolveValue(parameters.get(0),
                                    valueWrappers, executionContext));
                default:
                    List<Object> finalParams = new ArrayList<>(size);
                    for (Object param : parameters) {
                        finalParams.add(resolveValue(param, valueWrappers,
                                executionContext));
                    }
                    return Collections.unmodifiableList(finalParams);
                }
            } else {
                return parameters;
            }
        }

        private Map<String, Object> getFinalHash(
                ExecutionContext executionContext,
                List<ValueWrapper> valueWrappers) {
            if (isHashValuePlaceholderFound) {
                // At this point hash map is never empty
                int size = hash.size();
                switch (size) {
                case 1:
                    Entry<String, Object> singleEntry = hash.entrySet()
                            .iterator().next();
                    return Collections.singletonMap(singleEntry.getKey(),
                            resolveValue(singleEntry.getValue(), valueWrappers,
                                    executionContext));
                default:
                    Map<String, Object> finalHash = new HashMap<>();
                    for (Entry<String, Object> entry : hash.entrySet()) {
                        finalHash.put(entry.getKey(),
                                resolveValue(entry.getValue(), valueWrappers,
                                        executionContext));
                    }
                    return Collections.unmodifiableMap(finalHash);
                }
            } else {
                return hash;
            }
        }

        private Object resolveValue(Object value,
                List<ValueWrapper> valueWrappers,
                ExecutionContext executionContext) {
            if (value instanceof ValuePlaceholder) {
                if (value instanceof ListValuePlaceholder) {
                    ListValuePlaceholder listValues = (ListValuePlaceholder) value;
                    if (listValues.hasValuePlaceholderElement) {
                        ImmutableListBuilder<Object> builder = ImmutableList
                                .builder();
                        for (Object element : listValues) {
                            builder.add(resolveValue(element, valueWrappers,
                                    executionContext));
                        }
                        return builder.build();
                    } else {
                        // Values are immutable
                        return listValues.getValues();
                    }
                } else {
                    final ValueWrapper wrapper;
                    if (value instanceof DefaultValuePlaceholder) {
                        wrapper = ((DefaultValuePlaceholder) value)
                                .getProvider().get(executionContext);
                    } else {
                        wrapper = executionContext
                                .getValue(((ValuePlaceholder) value).getName());
                    }
                    valueWrappers.add(wrapper);
                    return wrapper.get();
                }
            } else {
                return value;
            }
        }

        private boolean initParamValuePlaceholderFound(
                List<Object> parameters) {
            if (parameters.isEmpty()) {
                return false;
            }
            for (Object param : parameters) {
                if (param instanceof ValuePlaceholder) {
                    return true;
                }
            }
            return false;
        }

        private boolean initHashValuePlaceholderFound(
                Map<String, Object> hash) {
            if (hash.isEmpty()) {
                return false;
            }
            for (Entry<String, Object> entry : hash.entrySet()) {
                if (entry.getValue() instanceof ValuePlaceholder) {
                    return true;
                }
            }
            return false;
        }

    }

    private static class DefaultOptions implements Options {

        private static final Logger LOGGER = LoggerFactory
                .getLogger(DefaultOptions.class);

        protected List<ValueWrapper> valueWrappers;

        protected Appendable appendable;

        protected int pushed;

        protected ExecutionContext executionContext;

        private final MustacheEngine engine;

        private final HelperAwareSegment segment;

        private final List<Object> parameters;

        private final Map<String, Object> hash;

        private final HelperDefinition originalDefinition;

        /**
         *
         * @param appendable
         * @param executionContext
         * @param segment
         * @param parameters
         * @param hash
         * @param valueWrappers
         * @param engine
         * @param originalDefinition
         */
        DefaultOptions(Appendable appendable, ExecutionContext executionContext,
                HelperAwareSegment segment, List<Object> parameters,
                Map<String, Object> hash, List<ValueWrapper> valueWrappers,
                MustacheEngine engine, HelperDefinition originalDefinition) {
            this.appendable = appendable;
            this.valueWrappers = valueWrappers;
            this.executionContext = executionContext;
            this.pushed = 0;
            this.segment = segment;
            this.parameters = parameters;
            this.hash = hash;
            this.engine = engine;
            this.originalDefinition = originalDefinition;
        }

        @Override
        public List<Object> getParameters() {
            return parameters;
        }

        @Override
        public Map<String, Object> getHash() {
            return hash;
        }

        @Override
        public void append(CharSequence sequence) {
            try {
                appendable.append(sequence);
            } catch (IOException e) {
                throw new MustacheException(MustacheProblem.RENDER_IO_ERROR, e);
            }
        }

        @Override
        public void fn() {
            appendable = segment.fn(appendable, executionContext);
        }

        @Override
        public void partial(String templateId) {
            partial(templateId, appendable);
        }

        @Override
        public void push(Object contextObject) {
            pushed++;
            executionContext = executionContext.setContextObject(contextObject);
        }

        @Override
        public Object pop() {
            if (pushed > 0) {
                pushed--;
                Object top = executionContext.getFirstContextObject();
                executionContext = executionContext.getParent();
                return top;
            }
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_POP_OPERATION);
        }

        @Override
        public Object peek() {
            return executionContext.getFirstContextObject();
        }

        @Override
        public Object getValue(String key) {
            if (valueWrappers == null) {
                valueWrappers = new ArrayList<>(5);
            }
            ValueWrapper wrapper = executionContext.getValue(key);
            valueWrappers.add(wrapper);
            return wrapper.get();
        }

        @Override
        public void partial(String templateId, Appendable appendable) {
            partial(templateId, appendable, executionContext);
        }

        @Override
        public void executeAsync(final HelperExecutable executable) {
            // For async execution we need to wrap the original appendable
            final AsyncAppendable asyncAppendable = new AsyncAppendable(
                    appendable);

            // Now submit the executable and get the future
            ExecutorService executor = engine.getConfiguration()
                    .geExecutorService();
            if (executor == null) {
                throw new MustacheException(
                        MustacheProblem.RENDER_ASYNC_PROCESSING_ERROR,
                        "ExecutorService must be set in order to submit an asynchronous task");
            }
            Future<AsyncAppendable> future = executor
                    .submit(() -> {
                        // We need a separate appendable for the async
                        // execution
                        DefaultOptions asyncOptions = new DefaultOptions(
                                new AsyncAppendable(asyncAppendable),
                                executionContext, segment, parameters, hash,
                                new ArrayList<>(), engine, originalDefinition);
                        executable.execute(asyncOptions);
                        return (AsyncAppendable) asyncOptions
                                .getAppendable();
                    });
            asyncAppendable.setFuture(future);
            this.appendable = asyncAppendable;
        }

        @Override
        public String source(String templateId) {
            Checker.checkArgumentNotEmpty(templateId);

            String mustacheSource = engine.getMustacheSource(templateId);

            if (mustacheSource == null) {
                throw new MustacheException(
                        MustacheProblem.RENDER_INVALID_PARTIAL_KEY,
                        "No mustache template found for the given key: %s %s",
                        templateId, segment.getOrigin());
            }
            return mustacheSource;
        }

        @Override
        public Appendable getAppendable() {
            return appendable;
        }

        @Override
        public void fn(Appendable appendable) {
            segment.fn(appendable, executionContext);
        }

        @Override
        public MustacheTagInfo getTagInfo() {
            return segment.getTagInfo();
        }

        @Override
        public String getContentLiteralBlock() {
            if (segment instanceof ContainerSegment) {
                return ((ContainerSegment) segment).getContentLiteralBlock();
            } else {
                return Strings.EMPTY;
            }
        }

        @Override
        public HelperDefinition getOriginalDefinition() {
            return originalDefinition;
        }

        protected void partial(String templateId, Appendable appendable,
                ExecutionContext executionContext) {
            Checker.checkArgumentsNotNull(templateId, appendable);

            Template partialTemplate = Segments.lookupTemplate(templateId,
                    engine, segment.getOrigin().getTemplate());

            if (partialTemplate == null) {
                throw new MustacheException(
                        MustacheProblem.RENDER_INVALID_PARTIAL_KEY,
                        "No partial found for the given key: %s %s", templateId,
                        segment.getOrigin());
            }
            // Note that indentation is not supported
            partialTemplate.getRootSegment().execute(appendable,
                    executionContext);
        }

        void release() {
            if (valueWrappers != null) {
                int wrappersSize = valueWrappers.size();
                if (wrappersSize == 1) {
                    valueWrappers.get(0).release();
                } else if (wrappersSize > 1) {
                    for (ValueWrapper wrapper : valueWrappers) {
                        wrapper.release();
                    }
                }
            }
            if (pushed > 0) {
                LOGGER.info(
                        "{} remaining objects pushed on the context stack will be automatically garbage collected [helperName: {}, template: {}]",
                        pushed, splitHelperName(segment.getTagInfo().getText(), segment).next(),
                        segment.getTagInfo().getTemplateName());
            }
        }

    }

    private static class DefaultValuePlaceholder implements ValuePlaceholder {

        private final String name;

        private final ValueProvider provider;

        DefaultValuePlaceholder(String name, MustacheEngine engine) {
            this.name = name;
            this.provider = new ValueProvider(name, engine.getConfiguration());
        }

        public String getName() {
            return name;
        }

        ValueProvider getProvider() {
            return provider;
        }

    }

    private static class ListValuePlaceholder
            implements ValuePlaceholder, Iterable<Object> {

        private final boolean hasValuePlaceholderElement;

        private final List<Object> values;

        private final String name;

        ListValuePlaceholder(String value, MustacheEngine engine,
                LiteralSupport literalSupport, HelperAwareSegment segment) {
            List<String> elements = Strings
                    .split(value.substring(1, value.length() - 1), ",");
            ImmutableListBuilder<Object> builder = ImmutableList.builder();
            for (String element : elements) {
                builder.add(getLiteralOrPlaceholder(element.trim(), engine, segment,
                        literalSupport));
            }
            this.values = builder.build();
            this.hasValuePlaceholderElement = initHasValuePlaceholderElement();
            this.name = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Iterator<Object> iterator() {
            return values.iterator();
        }

        List<Object> getValues() {
            return values;
        }

        private boolean initHasValuePlaceholderElement() {
            for (Object val : values) {
                if (val instanceof ValuePlaceholder) {
                    return true;
                }
            }
            return false;
        }

    }

}
