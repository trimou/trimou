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
package org.trimou.engine.parser;

import static org.trimou.engine.config.EngineConfigurationKey.REMOVE_STANDALONE_LINES;
import static org.trimou.engine.config.EngineConfigurationKey.REMOVE_UNNECESSARY_SEGMENTS;
import static org.trimou.engine.config.EngineConfigurationKey.REUSE_LINE_SEPARATOR_SEGMENTS;
import static org.trimou.exception.MustacheProblem.COMPILE_INVALID_TAG;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.segment.CommentSegment;
import org.trimou.engine.segment.ContainerSegment;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.engine.segment.ExtendSegment;
import org.trimou.engine.segment.InvertedSectionSegment;
import org.trimou.engine.segment.LineSeparatorSegment;
import org.trimou.engine.segment.Origin;
import org.trimou.engine.segment.PartialSegment;
import org.trimou.engine.segment.RootSegment;
import org.trimou.engine.segment.SectionSegment;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.SegmentType;
import org.trimou.engine.segment.SetDelimitersSegment;
import org.trimou.engine.segment.TextSegment;
import org.trimou.engine.segment.ValueSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Patterns;

import com.google.common.collect.ImmutableList;

/**
 * The default handler implementation that compiles the template. It's not
 * thread-safe and must not be reused.
 *
 * @author Martin Kouba
 */
class DefaultParsingHandler implements ParsingHandler {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultParsingHandler.class);

    private final Deque<ContainerSegmentBase> containerStack = new ArrayDeque<ContainerSegmentBase>();

    private final List<Template> nestedTemplates = new ArrayList<>();

    private MustacheEngine engine;

    private String templateName;

    private Delimiters delimiters;

    private Template template;

    private long start;

    private int line = 1;

    private int index = 0;

    private boolean skipValueEscaping;

    private boolean handlebarsSupportEnabled;

    private NestedTemplateBase currentNestedBase;

    @Override
    public void startTemplate(String name, Delimiters delimiters,
            MustacheEngine engine) {

        this.delimiters = delimiters;
        this.engine = engine;
        this.templateName = name;

        containerStack.addFirst(new RootSegmentBase());

        skipValueEscaping = engine.getConfiguration().getBooleanPropertyValue(
                EngineConfigurationKey.SKIP_VALUE_ESCAPING);
        handlebarsSupportEnabled = engine.getConfiguration()
                .getBooleanPropertyValue(
                        EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED);

        start = System.currentTimeMillis();
        logger.debug("Start compilation of {}", new Object[] { name });
    }

    @Override
    public void endTemplate() {

        RootSegmentBase rootSegmentBase = validate();

        // Post processing
        if (engine.getConfiguration()
                .getBooleanPropertyValue(REMOVE_STANDALONE_LINES)) {
            SegmentBases.removeStandaloneLines(rootSegmentBase);
        }
        if (engine.getConfiguration()
                .getBooleanPropertyValue(REMOVE_UNNECESSARY_SEGMENTS)) {
            SegmentBases.removeUnnecessarySegments(rootSegmentBase);
        }
        if (engine.getConfiguration()
                .getBooleanPropertyValue(REUSE_LINE_SEPARATOR_SEGMENTS)) {
            SegmentBases.reuseLineSeparatorSegments(rootSegmentBase);
        }

        template = new Template(engine.getConfiguration()
                .getIdentifierGenerator().generate(Mustache.class),
                templateName, engine, nestedTemplates);
        template.setRootSegment(rootSegmentBase.asSegment(template));
        for (Template nested : nestedTemplates) {
            nested.setParent(template);
        }

        logger.debug("Compilation of {} finished [time: {} ms, segments: {}]",
                new Object[] { templateName, System.currentTimeMillis() - start,
                        template.getRootSegment().getSegmentsSize(true) });

        rootSegmentBase = null;
        nestedTemplates.clear();
        containerStack.clear();
    }

    @Override
    public void text(String text) {
        addSegment(new SegmentBase(SegmentType.TEXT, text, line,
                incrementAndGetIndex()));
    }

    @Override
    public void tag(ParsedTag tag) {

        validateTag(tag);

        switch (tag.getType()) {
        case COMMENT:
            addSegment(new SegmentBase(tag, line, incrementAndGetIndex()));
            break;
        case UNESCAPE_VARIABLE:
        case VARIABLE:
            valueSegment(tag);
            break;
        case PARTIAL:
            addSegment(
                    new PartialSegmentBase(tag, line, incrementAndGetIndex()));
            break;
        case DELIMITER:
            changeDelimiters(tag.getContent());
            addSegment(new SegmentBase(tag, line, incrementAndGetIndex()));
            break;
        case SECTION:
        case INVERTED_SECTION:
        case EXTEND:
        case EXTEND_SECTION:
            push(new ContainerSegmentBase(tag, line, incrementAndGetIndex()));
            break;
        case NESTED_TEMPLATE:
            nestedTemplate(tag);
            break;
        case SECTION_END:
            endSection(tag.getContent());
            break;
        default:
            throw new IllegalStateException("Unsupported tag type");
        }
    }

    @Override
    public void lineSeparator(String separator) {
        addSegment(
                new LineSeparatorBase(separator, line, incrementAndGetIndex()));
        line++;
    }

    public Mustache getCompiledTemplate() {
        if (template == null) {
            throw new MustacheException(MustacheProblem.TEMPLATE_NOT_READY);
        }
        return template;
    }

    private void validateTag(ParsedTag tag) {

        if (StringUtils.isEmpty(tag.getContent())) {
            throw new MustacheException(COMPILE_INVALID_TAG,
                    "Tag has no content [type: %s, line: %s]", tag.getType(),
                    line);
        }

        if (tag.getContent().contains(delimiters.getStart())) {
            throw new MustacheException(COMPILE_INVALID_TAG,
                    "Tag content contains current start delimiter [type: %s, line: %s, delimiter: %s]",
                    tag.getType(), line, delimiters.getStart());
        }

        if (handlebarsSupportEnabled) {
            // Only validate segments which cannot have a HelperExecutionHandler
            // associated
            if (MustacheTagType.contentMustBeValidated(tag.getType())
                    && !MustacheTagType.supportsHelpers(tag.getType())
                    && StringUtils.containsWhitespace(tag.getContent())) {
                contentMustBeNonWhitespaceSequenceException(tag.getType());
            }
        } else {
            if (MustacheTagType
                    .contentMustBeNonWhitespaceCharacterSequence(tag.getType())
                    && StringUtils.containsWhitespace(tag.getContent())) {
                contentMustBeNonWhitespaceSequenceException(tag.getType());
            }
        }
    }

    private MustacheException contentMustBeNonWhitespaceSequenceException(
            MustacheTagType tagType) {
        throw new MustacheException(COMPILE_INVALID_TAG,
                "Tag content must be a non-whitespace character sequence [template: %s, type: %s, line: %s]",
                templateName, tagType, line);
    }

    private void endSection(String key) {

        ContainerSegmentBase container = pop();

        if (container == null || SegmentType.ROOT.equals(container.getType())
                || (!handlebarsSupportEnabled
                        && !key.equals(container.getContent()))
                || (handlebarsSupportEnabled
                        && !container.getContent().startsWith(key))
                || (handlebarsSupportEnabled
                        && !container.getContent().contains(" ")
                        && !key.equals(container.getContent()))) {
            // a) No container on the stack
            // b) Handlebars support not enabled and section start key does not
            // equal to section end key
            // c) Handlebars support enabled and section start key does not
            // start with section end key

            StringBuilder msg = new StringBuilder();
            List<String> params = new ArrayList<String>();
            msg.append("Invalid section end: ");
            if (container == null
                    || SegmentType.ROOT.equals(container.getType())) {
                msg.append("%s has no matching section start");
                params.add(key);
            } else {
                msg.append("%s is not matching section start %s");
                params.add(key);
                params.add(container.getContent());
            }
            msg.append(" [line: %s]");
            params.add("" + line);
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_SECTION_END, msg.toString(),
                    params.toArray());
        }

        if (container instanceof NestedTemplateBase) {
            // Do not add nested template as a segment
            NestedTemplateBase nestedBase = (NestedTemplateBase) container;
            Template nested = new Template(engine.getConfiguration()
                    .getIdentifierGenerator().generate(Mustache.class),
                    container.getContent(), engine);
            nested.setRootSegment(nestedBase.asSegment(nested));
            nestedTemplates.add(nested);
            currentNestedBase = null;
        } else {
            addSegment(container);
        }
    }

    /**
     * E.g. =<% %>=, =[ ]=
     *
     * @param key
     */
    private void changeDelimiters(String key) {

        if (key.charAt(0) != MustacheTagType.DELIMITER.getCommand()
                || key.charAt(key.length() - 1) != MustacheTagType.DELIMITER
                        .getCommand()) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_DELIMITERS,
                    "Invalid set delimiters tag: %s [line: %s]", key, line);
        }

        Matcher matcher = Patterns.newSetDelimitersContentPattern()
                .matcher(key.substring(1, key.length() - 1));

        if (matcher.find()) {
            delimiters.setNewValues(matcher.group(1), matcher.group(3));
        } else {
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_DELIMITERS,
                    "Invalid delimiters set: %s [line: %s]", key, line);
        }
    }

    /**
     * Push the container wrapper on the stack.
     *
     * @param container
     */
    private void push(ContainerSegmentBase container) {
        containerStack.addFirst(container);
        logger.trace("Push {} [name: {}]", container.getType(),
                container.getContent());
    }

    /**
     *
     * @return the container wrapper removed from the stack
     */
    private ContainerSegmentBase pop() {
        ContainerSegmentBase container = containerStack.removeFirst();
        logger.trace("Pop {} [name: {}]", container.getType(),
                container.getContent());
        return container;
    }

    /**
     * Add the segment to the container on the stack.
     *
     * @param segment
     */
    private void addSegment(SegmentBase segment) {
        containerStack.peekFirst().addSegment(segment);
        logger.trace("Add {}", segment);
    }

    /**
     * Validate the compiled template.
     */
    private RootSegmentBase validate() {

        ContainerSegmentBase root = containerStack.peekFirst();

        if (!(root instanceof RootSegmentBase)) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_TEMPLATE,
                    "Incorrect last container segment on the stack: %s",
                    containerStack.peekFirst().toString(), line);
        }
        return (RootSegmentBase) root;
    }

    private int incrementAndGetIndex() {
        return ++index;
    }

    private void valueSegment(ParsedTag tag) {
        addSegment(new ValueSegmentBase(tag, line, incrementAndGetIndex(),
                skipValueEscaping));
    }

    private void nestedTemplate(ParsedTag tag) {
        if (engine.getConfiguration().getBooleanPropertyValue(
                EngineConfigurationKey.NESTED_TEMPLATE_SUPPORT_ENABLED)) {
            // First check existing nested templates
            for (Template nested : nestedTemplates) {
                if (nested.getName().equals(tag.getContent())) {
                    throw new MustacheException(
                            MustacheProblem.COMPILE_NESTED_TEMPLATE_ERROR,
                            "A nested template with the name [%s] is already defined at line %s in the template [%s]",
                            tag.getContent(),
                            // The root segment of a nested template references
                            // the line from the defining template
                            nested.getRootSegment().getOrigin().getLine(),
                            templateName);
                }
            }
            NestedTemplateBase nestedBase = new NestedTemplateBase(
                    tag.getContent(), line, index);
            if (currentNestedBase != null) {
                throw new MustacheException(
                        MustacheProblem.COMPILE_NESTED_TEMPLATE_ERROR,
                        "Nested templates within nested template definitions are not supported: %s",
                        containerStack.peekFirst().toString());
            } else {
                currentNestedBase = nestedBase;
            }
            push(nestedBase);
        } else {
            valueSegment(
                    new ParsedTag(
                            MustacheTagType.NESTED_TEMPLATE.getCommand()
                                    + tag.getContent(),
                            MustacheTagType.VARIABLE));
        }
    }

    /**
     * Root segment
     */
    static class RootSegmentBase extends ContainerSegmentBase {

        RootSegmentBase() {
            super(SegmentType.ROOT, null, 0, 0);
        }

        @Override
        public RootSegment asSegment(Template template) {
            return new RootSegment(new Origin(template), getSegments(template));
        }

    }

    static class NestedTemplateBase extends ContainerSegmentBase {

        NestedTemplateBase(String content, int line, int index) {
            super(null, content, line, index);
        }

        @Override
        public RootSegment asSegment(Template template) {
            return new RootSegment(new Origin(template, getLine(), getIndex()),
                    getSegments(template));
        }

    }

    static class ContainerSegmentBase extends SegmentBase
            implements Iterable<SegmentBase> {

        private final List<SegmentBase> segments;

        ContainerSegmentBase(SegmentType type, String content, int line,
                int index) {
            super(type, content, line, index);
            this.segments = new ArrayList<SegmentBase>();
        }

        ContainerSegmentBase(ParsedTag tag, int line, int index) {
            super(tag, line, index);
            this.segments = new ArrayList<SegmentBase>();
        }

        boolean addSegment(SegmentBase segment) {
            if (SegmentType.EXTEND.equals(getType())
                    && !SegmentType.EXTEND_SECTION.equals(segment.getType())) {
                // Only add extending sections
                return false;
            }
            return segments.add(segment);
        }

        @Override
        ContainerSegment asSegment(Template template) {
            switch (getType()) {
            case SECTION:
                return new SectionSegment(getContent(), getOrigin(template),
                        getSegments(template));
            case INVERTED_SECTION:
                return new InvertedSectionSegment(getContent(),
                        getOrigin(template), getSegments(template));
            case EXTEND:
                return new ExtendSegment(getContent(), getOrigin(template),
                        getSegments(template));
            case EXTEND_SECTION:
                return new ExtendSectionSegment(getContent(),
                        getOrigin(template), getSegments(template));
            default:
                throw new IllegalStateException(
                        "Invalid tag type: " + getType());
            }
        }

        protected List<Segment> getSegments(Template template) {
            ImmutableList.Builder<Segment> builder = ImmutableList.builder();
            for (SegmentBase wrapper : segments) {
                builder.add(wrapper.asSegment(template));
            }
            return builder.build();
        }

        @Override
        public Iterator<SegmentBase> iterator() {
            return segments.iterator();
        }

        ListIterator<SegmentBase> listIterator() {
            return segments.listIterator();
        }

    }

    static class LineSeparatorBase extends SegmentBase {

        // Cache the segment so that reuse is possible
        private LineSeparatorSegment segment;

        public LineSeparatorBase(String content, int line, int index) {
            super(SegmentType.LINE_SEPARATOR, content, line, index);
        }

        @Override
        Segment asSegment(Template template) {
            if (segment == null) {
                segment = new LineSeparatorSegment(getContent(),
                        getOrigin(template));
            }
            return segment;
        }

    }

    static class ValueSegmentBase extends SegmentBase {

        private boolean unescape;

        ValueSegmentBase(ParsedTag tag, int line, int index,
                boolean skipValueEscaping) {
            super(SegmentType.VALUE, tag.getContent(), line, index);
            unescape = skipValueEscaping ? true
                    : tag.getType().equals(MustacheTagType.UNESCAPE_VARIABLE);
        }

        @Override
        ValueSegment asSegment(Template template) {
            return new ValueSegment(getContent(), getOrigin(template),
                    unescape);
        }

    }

    static class PartialSegmentBase extends SegmentBase {

        private String indentation;

        PartialSegmentBase(ParsedTag tag, int line, int index) {
            super(tag, line, index);
        }

        public void setIndentation(String indentation) {
            this.indentation = indentation;
        }

        @Override
        public PartialSegment asSegment(Template template) {
            return new PartialSegment(getContent(), getOrigin(template),
                    indentation);
        }

    }

    static class SegmentBase {

        private final SegmentType type;

        private final String content;

        private final int line;

        private final int index;

        SegmentBase(ParsedTag tag, int line, int index) {
            this.content = tag.getContent();
            this.type = SegmentType.fromTag(tag.getType());
            this.line = line;
            this.index = index;
        }

        SegmentBase(SegmentType type, String content, int line, int index) {
            this.type = type;
            this.content = content;
            this.line = line;
            this.index = index;
        }

        SegmentType getType() {
            return type;
        }

        String getContent() {
            return content;
        }

        int getLine() {
            return line;
        }

        int getIndex() {
            return index;
        }

        Segment asSegment(Template template) {
            switch (type) {
            case TEXT:
                return new TextSegment(content, getOrigin(template));
            case COMMENT:
                return new CommentSegment(content, getOrigin(template));
            case DELIMITERS:
                return new SetDelimitersSegment(content, getOrigin(template));
            default:
                throw new IllegalStateException(
                        "Unsupported segment type: " + type);
            }
        }

        protected Origin getOrigin(Template template) {
            return new Origin(template, line, index);
        }

        @Override
        public String toString() {
            return String.format("[type: %s, content: %s, line: %s, idx: %s]",
                    type, content, line, index);
        }

    }

}
