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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.segment.AbstractContainerSegment;
import org.trimou.engine.segment.CommentSegment;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.engine.segment.ExtendSegment;
import org.trimou.engine.segment.InvertedSectionSegment;
import org.trimou.engine.segment.LineSeparatorSegment;
import org.trimou.engine.segment.PartialSegment;
import org.trimou.engine.segment.SectionSegment;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.SetDelimitersSegment;
import org.trimou.engine.segment.TemplateSegment;
import org.trimou.engine.segment.TextSegment;
import org.trimou.engine.segment.ValueSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * The default handler implementation that compiles the template. It's not
 * thread-safe and should not be reused.
 *
 * @author Martin Kouba
 */
public class DefaultParsingHandler implements ParsingHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultParsingHandler.class);

	private Deque<AbstractContainerSegment> containerStack = new ArrayDeque<AbstractContainerSegment>();

	private TemplateSegment template;

	private Delimiters delimiters;

	private long start;

	private long segments = 0;

	@Override
	public void startTemplate(String name, Delimiters delimiters,
			MustacheEngine engine) {

		this.delimiters = delimiters;
		template = new TemplateSegment(name, engine);
		containerStack.addFirst(template);

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
		addSegment(new TextSegment(text, template));
	}

	@Override
	public void tag(ParsedTag tag) {

		switch (tag.getType()) {
		case VARIABLE:
			addSegment(new ValueSegment(tag.getContent(), template, false));
			break;
		case UNESCAPE_VARIABLE:
			addSegment(new ValueSegment(tag.getContent(), template, true));
			break;
		case COMMENT:
			addSegment(new CommentSegment(tag.getContent(), template));
			break;
		case DELIMITER:
			changeDelimiters(tag.getContent());
			addSegment(new SetDelimitersSegment(tag.getContent(), template));
			break;
		case SECTION:
			push(new SectionSegment(tag.getContent(), template));
			break;
		case INVERTED_SECTION:
			push(new InvertedSectionSegment(tag.getContent(), template));
			break;
		case SECTION_END:
			endSection(tag.getContent());
			break;
		case PARTIAL:
			addSegment(new PartialSegment(tag.getContent(), template));
			break;
		case EXTEND:
			push(new ExtendSegment(tag.getContent(), template));
			break;
		case EXTEND_SECTION:
			push(new ExtendSectionSegment(tag.getContent(), template));
			break;
		default:
			break;
		}
	}

	@Override
	public void lineSeparator(String separator) {
		addSegment(new LineSeparatorSegment(separator, template));
	}

	/**
	 * @return the compiled template
	 * @throws IllegalStateException
	 *             If not finished yet
	 */
	public TemplateSegment getCompiledTemplate() {
		if (!template.isReadOnly()) {
			throw new MustacheException(MustacheProblem.TEMPLATE_NOT_READY);
		}
		return template;
	}

	private void endSection(String key) {
		AbstractContainerSegment container = pop();
		if (container == null || !key.equals(container.getText())) {
			throw new MustacheException(
					MustacheProblem.COMPILE_INVALID_SECTION_END);
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
					MustacheProblem.COMPILE_INVALID_DELIMITERS);
		}

		Matcher matcher = Pattern.compile("([[^=]&&\\S]+)(\\s+)([[^=]&&\\S]+)")
				.matcher(key.substring(1, key.length() - 1));

		if (matcher.find()) {
			delimiters.setNewValues(matcher.group(1), matcher.group(3));
		} else {
			throw new MustacheException(
					MustacheProblem.COMPILE_INVALID_DELIMITERS);
		}
	}

	/**
	 * Push the container on the stack.
	 *
	 * @param container
	 */
	private void push(AbstractContainerSegment container) {
		containerStack.addFirst(container);
		logger.trace("Push {} [name: {}]", container.getType(),
				container.getText());
	}

	/**
	 *
	 * @return the container removed from the stack
	 */
	private AbstractContainerSegment pop() {
		AbstractContainerSegment container = containerStack.removeFirst();
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

	/**
	 * Validate the compiled template.
	 *
	 * TODO add more validations
	 */
	private void validate() {

		if (!containerStack.peekFirst().equals(template)) {
			throw new MustacheException(
					MustacheProblem.COMPILE_INVALID_TEMPLATE,
					"Incorrect last container segment on the stack: "
							+ containerStack.peekFirst().toString());
		}
	}

}
