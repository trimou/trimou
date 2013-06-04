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

import static org.trimou.util.Strings.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Segment} utils.
 *
 * @author Martin Kouba
 */
final class Segments {

	private static final Logger logger = LoggerFactory.getLogger(Segments.class);

	private Segments() {
	}

	static void removeStandaloneLines(TemplateSegment template) {

		List<List<Segment>> lines = readSegmentLines(template);

		// First identify the segments to remove
		Set<Segment> segmentsToRemove = new HashSet<Segment>();
		int idx = 0;
		for (List<Segment> line : lines) {
			idx++;
			if (isStandaloneLine(line)) {

				// Extract indentation for standalone partial segment
				extractIndentationForPartial(line);

				for (Segment segment : line) {
					if (segment instanceof AbstractContainerSegment
							|| SegmentType.PARTIAL
									.equals(segment.getType())) {
						continue;
					}
					segmentsToRemove.add(segment);
				}
				logger.trace("Segment line {} is standalone", idx);
			}
		}

		// Then remove segments
		if (!segmentsToRemove.isEmpty()) {
			removeSegments(segmentsToRemove, template);
			logger.debug("{} segments removed", segmentsToRemove.size());
		}

	}

	/**
	 *
	 * @param standaloneLine
	 */
	static void extractIndentationForPartial(List<Segment> standaloneLine) {

		if (SegmentType.TEXT.equals(standaloneLine.get(0).getType())) {

			// First segment is whitespace - try to find partial
			PartialSegment partial = null;
			for (Segment segment : standaloneLine) {
				if (SegmentType.PARTIAL.equals(segment.getType())) {
					partial = (PartialSegment) segment;
					break;
				}
			}

			if (partial != null) {
				partial.setIndentation(standaloneLine.get(0).getText());
			}
		}
	}

	static void removeUnnecessarySegments(AbstractContainerSegment container) {

		for (Iterator<Segment> iterator = container.iterator(); iterator
				.hasNext();) {
			Segment segment = iterator.next();
			if (segment instanceof AbstractContainerSegment) {
				removeUnnecessarySegments((AbstractContainerSegment) segment);
			} else {
				if (SegmentType.COMMENT.equals(segment.getType())
						|| SegmentType.DELIMITERS.equals(segment.getType())) {
					iterator.remove();
				}
			}
		}
	}

	static void removeSegments(Set<Segment> segmentsToRemove,
			AbstractContainerSegment container) {

		for (Iterator<Segment> iterator = container.iterator(); iterator
				.hasNext();) {
			Segment segment = iterator.next();
			if (segment instanceof AbstractContainerSegment) {
				removeSegments(segmentsToRemove, (AbstractContainerSegment) segment);
			} else {
				if (segmentsToRemove.contains(segment)) {
					iterator.remove();
				}
			}
		}
	}

	static boolean isStandaloneLine(List<Segment> line) {

		boolean standaloneCandidate = false;

		for (Segment segment : line) {
			if (SegmentType.VALUE.equals(segment.getType())) {
				// Value segment
				return false;
			} else if (SegmentType.TEXT.equals(segment.getType())) {
				if (!StringUtils.isWhitespace(segment.getText())) {
					// Text segment with non-whitespace character
					return false;
				}
			} else if (segment.getType().isStandaloneCandidate()) {
				standaloneCandidate = true;
			}
		}
		return standaloneCandidate;
	}

	/**
	 * Read segment lines recursively.
	 *
	 * @param container
	 * @return
	 */
	static List<List<Segment>> readSegmentLines(
			AbstractContainerSegment container) {
		List<List<Segment>> lines = new ArrayList<List<Segment>>();
		// Add the last line manually - there is no line separator to trigger
		// flush
		lines.add(readSegmentLines(lines, null, container));
		return lines;
	}

	/**
	 * Read segment lines before rendering.
	 *
	 * @param container
	 * @return
	 */
	static List<List<Segment>> readSegmentLinesBeforeRendering(
			AbstractContainerSegment container) {
		List<List<Segment>> lines = new ArrayList<List<Segment>>();
		List<Segment> currentLine = new ArrayList<Segment>();

		for (Segment segment : container) {
			if (!SegmentType.LINE_SEPARATOR.equals(segment.getType())) {
				currentLine.add(segment);
			} else {
				// New line separator - flush the line
				currentLine.add(segment);
				lines.add(currentLine);
				currentLine = new ArrayList<Segment>();
			}
		}
		// Add the last line manually - there is no line separator to trigger
		// flush
		if (!currentLine.isEmpty()) {
			lines.add(currentLine);
		}
		return lines;
	}

	static List<Segment> readSegmentLines(List<List<Segment>> lines,
			List<Segment> currentLine, AbstractContainerSegment container) {

		if (currentLine == null) {
			currentLine = new ArrayList<Segment>();
		}

		if (!SegmentType.TEMPLATE.equals(container.getType())) {
			// Simulate the start tag
			currentLine.add(container);
		}

		for (Segment segment : container) {
			if (segment instanceof AbstractContainerSegment) {
				currentLine = readSegmentLines(lines, currentLine,
						(AbstractContainerSegment) segment);
			} else if (!SegmentType.LINE_SEPARATOR.equals(segment.getType())) {
				currentLine.add(segment);
			} else {
				// New line separator - flush the line
				currentLine.add(segment);
				lines.add(currentLine);
				currentLine = new ArrayList<Segment>();
			}
		}

		if (!SegmentType.TEMPLATE.equals(container.getType())) {
			// Simulate the end tag
			currentLine.add(container);
		}
		return currentLine;
	}

	/**
	 *
	 * @param container
	 * @return simple tree vizualization, for debug purpose only
	 */
	static String getSegmentTree(AbstractContainerSegment container) {
		return getSegmentTreeInternal(1, container);
	}

	private static String getSegmentTreeInternal(int level,
			AbstractContainerSegment container) {

		StringBuilder tree = new StringBuilder();
		tree.append(LINE_SEPARATOR);
		if (level > 1) {
			tree.append(StringUtils.repeat(" ", level - 1));
		}
		tree.append("+");
		if (!SegmentType.TEMPLATE.equals(container.getType())) {
			tree.append(container.getTemplate().getText());
			tree.append(":");
		}
		tree.append(container.getType());
		tree.append(":");
		tree.append(container.getText());
		for (Segment segment : container.getSegments()) {
			if (segment instanceof AbstractContainerSegment) {
				tree.append(getSegmentTreeInternal(level + 1,
						(AbstractContainerSegment) segment));
			} else {
				tree.append(LINE_SEPARATOR);
				tree.append(StringUtils.repeat(" ", level));
				tree.append("-");
				tree.append(segment.getOrigin().getTemplateName());
				tree.append(":");
				tree.append(segment.getType());
				if (segment.getType().hasName()) {
					tree.append(":");
					tree.append(segment.getText());
				}
			}
		}
		return tree.toString();
	}

}
