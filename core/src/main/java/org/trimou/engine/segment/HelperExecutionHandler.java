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

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ExecutionContext.TargetStack;
import org.trimou.engine.context.ValueWrapper;
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

    private static Splitter hashEntrySplitter = Splitter.on(Strings.EQUALS)
            .omitEmptyStrings();

    private static Pattern literalPattern = Patterns
            .newHelperStringLiteralPattern();

    private final Helper helper;

    private final OptionsBuilder optionsBuilder;

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

        Iterator<String> result = splitHelperName(name);
        String firstToken = result.next();

        Helper helper = engine.getConfiguration().getHelpers().get(firstToken);

        if (helper == null) {
            return null;
        }

        ImmutableList.Builder<Object> params = ImmutableList.builder();
        ImmutableMap.Builder<String, Object> hash = ImmutableMap.builder();

        while (result.hasNext()) {
            String paramOrHash = result.next();
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

    private static Object getLiteralOrPlaceholder(String value) {
        Matcher matcher = literalPattern.matcher(value);
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            return new DefaultValuePlaceholder(value);
        }
    }

    private HelperExecutionHandler(Helper helper, OptionsBuilder optionsBuilder) {
        this.helper = helper;
        this.optionsBuilder = optionsBuilder;
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

    private static class OptionsBuilder implements HelperDefinition {

        private final List<Object> parameters;

        private final Map<String, Object> hash;

        private final HelperAwareSegment segment;

        private final MustacheEngine engine;

        private OptionsBuilder(List<Object> parameters,
                Map<String, Object> hash, HelperAwareSegment segment,
                MustacheEngine engine) {
            this.parameters = parameters;
            this.hash = hash;
            this.segment = segment;
            this.engine = engine;
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
            List<Object> params = null;
            Map<String, Object> optionalHash = null;

            if (Checker.isNullOrEmpty(parameters)) {
                params = Collections.emptyList();
            } else {
                params = new ArrayList<Object>();
                for (Object param : parameters) {
                    if (param instanceof ValuePlaceholder) {
                        ValueWrapper wrapper = executionContext
                                .getValue(((ValuePlaceholder) param).getName());
                        valueWrappers.add(wrapper);
                        params.add(wrapper.get());
                    } else {
                        params.add(param);
                    }
                }
                params = Collections.unmodifiableList(params);
            }

            if (Checker.isNullOrEmpty(hash)) {
                optionalHash = Collections.emptyMap();
            } else {
                optionalHash = new HashMap<String, Object>();
                for (Entry<String, Object> hashEntry : hash.entrySet()) {
                    if (hashEntry.getValue() instanceof ValuePlaceholder) {
                        ValueWrapper wrapper = executionContext
                                .getValue(((ValuePlaceholder) hashEntry
                                        .getValue()).getName());
                        valueWrappers.add(wrapper);
                        optionalHash.put(hashEntry.getKey(), wrapper.get());
                    } else {
                        optionalHash.put(hashEntry.getKey(),
                                hashEntry.getValue());
                    }
                }
                optionalHash = Collections.unmodifiableMap(optionalHash);
            }

            return new DefaultOptions(appendable, executionContext, segment,
                    params, optionalHash, valueWrappers.build(), engine);
        }

    }

    private static class DefaultOptions implements Options {

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

            TemplateSegment partialTemplate = (TemplateSegment) engine
                    .getMustache(templateId);

            if (partialTemplate == null) {
                throw new MustacheException(
                        MustacheProblem.RENDER_INVALID_PARTIAL_KEY,
                        "No partial found for the given key: %s %s",
                        templateId, segment.getOrigin());
            }

            executionContext.push(TEMPLATE_INVOCATION, partialTemplate);
            // Indentation is not supported
            partialTemplate.execute(appendable, executionContext);
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
        public MustacheTagInfo getTagInfo() {
            return segment.getTagInfo();
        }

        void release() {
            for (ValueWrapper wrapper : valueWrappers) {
                wrapper.release();
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
     * TODO possibly rewrite as this implementation is quite naive.
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
