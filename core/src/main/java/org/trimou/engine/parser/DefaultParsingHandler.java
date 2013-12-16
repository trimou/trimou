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

import static org.trimou.exception.MustacheProblem.COMPILE_INVALID_TAG;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.trimou.engine.segment.SectionSegment;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.SegmentType;
import org.trimou.engine.segment.SetDelimitersSegment;
import org.trimou.engine.segment.TemplateSegment;
import org.trimou.engine.segment.TextSegment;
import org.trimou.engine.segment.ValueSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Patterns;

/**
 * The default handler implementation that compiles the template. It's not
 * thread-safe and should not be reused.
 *
 * @author Martin Kouba
 */
class DefaultParsingHandler implements ParsingHandler {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultParsingHandler.class);

    private static Pattern handlebarsNameValidationPattern = Patterns
            .newHandlebarsNameValidationPattern();

    private Deque<ContainerSegment> containerStack = new ArrayDeque<ContainerSegment>();

    private TemplateSegment template;

    private Delimiters delimiters;

    private long start;

    private int segments = 0;

    private int line = 1;

    private boolean skipValueEscaping;

    private boolean handlebarsSupportEnabled;

    @Override
    public void startTemplate(String name, Delimiters delimiters,
            MustacheEngine engine) {
        this.delimiters = delimiters;
        template = new TemplateSegment(name, engine);
        containerStack.addFirst(template);
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
        validate();
        template.performPostProcessing();
        logger.debug("Compilation of {} finished [time: {} ms, segments: {}]",
                new Object[] { template.getText(),
                        System.currentTimeMillis() - start, segments });
    }

    @Override
    public void text(String text) {
        addSegment(new TextSegment(text, new Origin(template, line)));
    }

    @Override
    public void tag(ParsedTag tag) {

        validateTag(tag);

        switch (tag.getType()) {
        case VARIABLE:
            addValueSegment(tag.getContent(), false);
            break;
        case UNESCAPE_VARIABLE:
            addValueSegment(tag.getContent(), true);
            break;
        case COMMENT:
            addSegment(new CommentSegment(tag.getContent(), new Origin(
                    template, line)));
            break;
        case DELIMITER:
            changeDelimiters(tag.getContent());
            addSegment(new SetDelimitersSegment(tag.getContent(), new Origin(
                    template, line)));
            break;
        case SECTION:
            push(new SectionSegment(tag.getContent(),
                    new Origin(template, line)));
            break;
        case INVERTED_SECTION:
            push(new InvertedSectionSegment(tag.getContent(), new Origin(
                    template, line)));
            break;
        case SECTION_END:
            endSection(tag.getContent());
            break;
        case PARTIAL:
            addSegment(new PartialSegment(tag.getContent(), new Origin(
                    template, line)));
            break;
        case EXTEND:
            push(new ExtendSegment(tag.getContent(), new Origin(template, line)));
            break;
        case EXTEND_SECTION:
            push(new ExtendSectionSegment(tag.getContent(), new Origin(
                    template, line)));
            break;
        default:
            break;
        }
    }

    @Override
    public void lineSeparator(String separator) {
        addSegment(new LineSeparatorSegment(separator, new Origin(template,
                line)));
        line++;
    }

    public Mustache getCompiledTemplate() {
        if (!template.isReadOnly()) {
            throw new MustacheException(MustacheProblem.TEMPLATE_NOT_READY,
                    template.getName());
        }
        return template;
    }

    private void validateTag(ParsedTag tag) {

        if (StringUtils.isEmpty(tag.getContent())) {
            throw new MustacheException(COMPILE_INVALID_TAG,
                    "Tag has no content [type: %s, line: %s]", tag.getType(),
                    line);
        }

        if (handlebarsSupportEnabled
                && MustacheTagType.contentMustBeValidated(tag.getType())) {
            if (!handlebarsNameValidationPattern.matcher(tag.getContent())
                    .matches()) {
                throw new MustacheException(
                        COMPILE_INVALID_TAG,
                        "Invalid tag content detected [template: %s, type: %s, line: %s]",
                        template.getName(), tag.getType(), line);
            }
        } else {
            if (MustacheTagType.contentMustBeNonWhitespaceCharacterSequence(tag
                    .getType())
                    && StringUtils.containsWhitespace(tag.getContent())) {
                throw new MustacheException(
                        COMPILE_INVALID_TAG,
                        "Tag content must be a non-whitespace character sequence [template: %s, type: %s, line: %s]",
                        template.getName(), tag.getType(), line);
            }
        }
    }

    private void endSection(String key) {
        ContainerSegment container = pop();
        // FIXME better check?
        if (container == null
                || (!handlebarsSupportEnabled
                        && !key.equals(container.getText()) || (handlebarsSupportEnabled && !container
                        .getText().startsWith(key)))) {
            StringBuilder msg = new StringBuilder();
            List<String> params = new ArrayList<String>();
            msg.append("Invalid section end: ");
            if (container == null
                    || SegmentType.TEMPLATE.equals(container.getType())) {
                msg.append("%s has no matching section start");
                params.add(key);
            } else {
                msg.append("%s is not matching section start %s");
                params.add(key);
                params.add(container.getText());
            }
            msg.append(" [line: %s]");
            params.add("" + line);
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_SECTION_END,
                    msg.toString(), params.toArray());
        }
        addSegment(container);
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

        Matcher matcher = Patterns.newSetDelimitersContentPattern().matcher(
                key.substring(1, key.length() - 1));

        if (matcher.find()) {
            delimiters.setNewValues(matcher.group(1), matcher.group(3));
        } else {
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_DELIMITERS,
                    "Invalid delimiters set: %s [line: %s]", key, line);
        }
    }

    /**
     * Push the container on the stack.
     *
     * @param container
     */
    private void push(ContainerSegment container) {
        containerStack.addFirst(container);
        logger.trace("Push {} [name: {}]", container.getType(),
                container.getText());
    }

    /**
     *
     * @return the container removed from the stack
     */
    private ContainerSegment pop() {
        ContainerSegment container = containerStack.removeFirst();
        logger.trace("Pop {} [name: {}]", container.getType(),
                container.getText());
        return container;
    }

    /**
     * Add the segment to the top container on the stack.
     *
     * @param segment
     */
    private void addSegment(Segment segment) {
        segments++;
        containerStack.peekFirst().addSegment(segment);
        logger.trace("Add {}", segment);
    }

    private void addValueSegment(String text, boolean unescape) {
        if (skipValueEscaping) {
            addSegment(new ValueSegment(text, new Origin(template, line), true));
        } else {
            addSegment(new ValueSegment(text, new Origin(template, line),
                    unescape));
        }
    }

    /**
     * Validate the compiled template.
     *
     * TODO add more validations
     */
    private void validate() {

        if (!containerStack.peekFirst().equals(template)) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_INVALID_TEMPLATE,
                    "Incorrect last container segment on the stack: %s [line: %s]",
                    containerStack.peekFirst().toString(), line);
        }
    }

}
