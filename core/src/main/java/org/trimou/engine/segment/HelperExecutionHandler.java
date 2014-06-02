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

import static org.trimou.engine.context.ExecutionContext.TargetStack.TEMPLATE_INVOCATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ExecutionContext.TargetStack;
import org.trimou.engine.context.ValueWrapper;
import org.trimou.engine.parser.Template;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Helper;
import org.trimou.handlebars.HelperDefinition;
import org.trimou.handlebars.HelperDefinition.ValuePlaceholder;
import org.trimou.handlebars.Options;
import org.trimou.util.Checker;
import org.trimou.util.Patterns;
import org.trimou.util.Strings;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Wraps {@link Helper} instance and handles its execution (e.g. builds
 * {@link Options} instance).
 *
 * @author Martin Kouba
 * @see HelperAwareSegment
 */
class HelperExecutionHandler {

    private static final Splitter hashEntrySplitter = Splitter.on(
            Strings.EQUALS).omitEmptyStrings();

    private static final Pattern literalPattern = Patterns
            .newHelperStringLiteralPattern();

    private final Helper helper;

    private final OptionsBuilder optionsBuilder;

    /**
     *
     * @param helper
     * @param optionsBuilder
     */
    private HelperExecutionHandler(Helper helper, OptionsBuilder optionsBuilder) {
        this.helper = helper;
        this.optionsBuilder = optionsBuilder;
    }

    /**
     *
     * @param name
     * @param configuration
     * @param segment
     * @return a handler for the given name or <code>null</code> if no such
     *         helper exists
     */
    static HelperExecutionHandler from(String name, MustacheEngine engine,
            HelperAwareSegment segment) {

        Iterator<String> parts = splitHelperName(name);
        Helper helper = engine.getConfiguration().getHelpers()
                .get(parts.next());

        if (helper == null) {
            return null;
        }

        ImmutableList.Builder<Object> params = ImmutableList.builder();
        ImmutableMap.Builder<String, Object> hash = ImmutableMap.builder();

        while (parts.hasNext()) {
            String paramOrHash = parts.next();
            if (paramOrHash.contains(Strings.EQUALS)) {
                Iterator<String> hashResult = hashEntrySplitter.split(
                        paramOrHash).iterator();
                hash.put(hashResult.next(),
                        getLiteralOrPlaceholder(hashResult.next()));
            } else {
                params.add(getLiteralOrPlaceholder(paramOrHash));
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
    void execute(Appendable appendable, ExecutionContext executionContext) {

        DefaultOptions options = optionsBuilder.build(appendable,
                executionContext);
        try {
            helper.execute(options);
        } finally {
            options.release();
        }
    }

    private static Object getLiteralOrPlaceholder(String value) {
        Matcher matcher = literalPattern.matcher(value);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            return new DefaultValuePlaceholder(value);
        }
    }

    private static class OptionsBuilder implements HelperDefinition {

        private final List<Object> parameters;

        private final Map<String, Object> hash;

        private final HelperAwareSegment segment;

        private final MustacheEngine engine;

        private final boolean isParamValuePlaceholderFound;

        private final boolean isHashValuePlaceholderFound;

        private OptionsBuilder(List<Object> parameters,
                Map<String, Object> hash, HelperAwareSegment segment,
                MustacheEngine engine) {
            this.parameters = parameters;
            this.hash = hash;
            this.segment = segment;
            this.engine = engine;
            this.isParamValuePlaceholderFound = initParamValuePlaceholderFound(parameters);
            this.isHashValuePlaceholderFound = initHashValuePlaceholderFound(hash);
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

        public DefaultOptions build(Appendable appendable,
                ExecutionContext executionContext) {

            ImmutableList.Builder<ValueWrapper> valueWrappers = ImmutableList
                    .builder();
            List<Object> finalParams;
            Map<String, Object> finalHash;

            if (isParamValuePlaceholderFound) {
                finalParams = new ArrayList<Object>();
                for (Object param : parameters) {
                    if (param instanceof ValuePlaceholder) {
                        ValueWrapper wrapper = executionContext
                                .getValue(((ValuePlaceholder) param).getName());
                        valueWrappers.add(wrapper);
                        finalParams.add(wrapper.get());
                    } else {
                        finalParams.add(param);
                    }
                }
                finalParams = Collections.unmodifiableList(finalParams);
            } else {
                finalParams = parameters;
            }

            if(isHashValuePlaceholderFound) {
                finalHash = new HashMap<String, Object>();
                for (Entry<String, Object> hashEntry : hash.entrySet()) {
                    if (hashEntry.getValue() instanceof ValuePlaceholder) {
                        ValueWrapper wrapper = executionContext
                                .getValue(((ValuePlaceholder) hashEntry
                                        .getValue()).getName());
                        valueWrappers.add(wrapper);
                        finalHash.put(hashEntry.getKey(), wrapper.get());
                    } else {
                        finalHash.put(hashEntry.getKey(),
                                hashEntry.getValue());
                    }
                }
                finalHash = Collections.unmodifiableMap(finalHash);
            } else {
                finalHash = hash;
            }

            return new DefaultOptions(appendable, executionContext, segment,
                    finalParams, finalHash, valueWrappers.build(), engine);
        }

        private boolean initParamValuePlaceholderFound(List<Object> parameters) {
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

        private boolean initHashValuePlaceholderFound(Map<String, Object> hash) {
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

        private static final Logger logger = LoggerFactory
                .getLogger(DefaultOptions.class);

        private final MustacheEngine engine;

        private final List<ValueWrapper> valueWrappers;

        private int pushed = 0;

        private final ExecutionContext executionContext;

        private final HelperAwareSegment segment;

        private final List<Object> parameters;

        private final Map<String, Object> hash;

        private final Appendable appendable;

        public DefaultOptions(Appendable appendable,
                ExecutionContext executionContext, HelperAwareSegment segment,
                List<Object> parameters, Map<String, Object> hash,
                List<ValueWrapper> valueWrappers, MustacheEngine engine) {
            this.appendable = appendable;
            this.executionContext = executionContext;
            this.segment = segment;
            this.parameters = parameters;
            this.hash = hash;
            this.valueWrappers = valueWrappers;
            this.engine = engine;
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
            segment.fn(appendable, executionContext);
        }

        @Override
        public void partial(String templateId) {
            Checker.checkArgumentNotEmpty(templateId);

            Template partialTemplate = (Template) engine
                    .getMustache(templateId);

            if (partialTemplate == null) {
                throw new MustacheException(
                        MustacheProblem.RENDER_INVALID_PARTIAL_KEY,
                        "No partial found for the given key: %s %s",
                        templateId, segment.getOrigin());
            }

            executionContext.push(TEMPLATE_INVOCATION, partialTemplate);
            // Indentation is not supported
            partialTemplate.getRootSegment().execute(appendable,
                    executionContext);
            executionContext.pop(TEMPLATE_INVOCATION);
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
        public void push(Object contextObject) {
            pushed++;
            executionContext.push(TargetStack.CONTEXT, contextObject);
        }

        @Override
        public Object pop() {
            if (pushed > 0) {
                pushed--;
                return executionContext.pop(TargetStack.CONTEXT);
            }
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_POP_OPERATION);
        }

        @Override
        public Object peek() {
            return executionContext.peek(TargetStack.CONTEXT);
        }

        @Override
        public MustacheTagInfo getTagInfo() {
            return segment.getTagInfo();
        }

        void release() {
            for (ValueWrapper wrapper : valueWrappers) {
                wrapper.release();
            }
            if (pushed > 0) {
                // Remove all remaining pushed objects at the end of helper
                // execution
                for (int i = 0; i < pushed; i++) {
                    executionContext.pop(TargetStack.CONTEXT);

                }
                logger.warn(
                        "Cleaned up {} objects pushed on the context stack [helperName: {}, template: {}]",
                        new Object[] { pushed,
                                splitHelperName(getTagInfo().getText()).next(),
                                getTagInfo().getTemplateName() });
            }
        }

    }

    private static class DefaultValuePlaceholder implements ValuePlaceholder {

        private final String name;

        public DefaultValuePlaceholder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * This implementation is quite naive and should be possibly rewritten.
     *
     * @param name
     * @return
     */
    static Iterator<String> splitHelperName(String name) {

        boolean literal = false;
        boolean whitespace = false;
        List<String> parts = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ') {
                if (!whitespace) {
                    if (!literal) {
                        parts.add(buffer.toString());
                        buffer = new StringBuilder();
                        whitespace = true;
                    } else {
                        buffer.append(name.charAt(i));
                    }
                }
            } else {
                if (name.charAt(i) == '"') {
                    if (literal) {
                        literal = false;
                    } else {
                        literal = true;
                    }
                }
                whitespace = false;
                buffer.append(name.charAt(i));
            }
        }

        if (buffer.length() > 0) {
            parts.add(buffer.toString());
        }
        return parts.iterator();
    }

}
