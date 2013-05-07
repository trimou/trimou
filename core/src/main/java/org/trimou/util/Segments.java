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
package org.trimou.util;

import static org.trimou.util.Strings.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.trimou.engine.segment.ContainerSegment;
import org.trimou.engine.segment.Segment;
import org.trimou.engine.segment.SegmentType;

/**
 * {@link Segment} utils.
 *
 * @author Martin Kouba
 */
public final class Segments {

	private Segments() {
	}

	/**
	 * Read segment lines recursively.
	 *
	 * @param container
	 * @return
	 */
	public static List<List<Segment>> readSegmentLines(
			ContainerSegment container) {
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
	public static List<List<Segment>> readSegmentLinesBeforeRendering(
			ContainerSegment container) {
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

	private static List<Segment> readSegmentLines(List<List<Segment>> lines,
			List<Segment> currentLine, ContainerSegment container) {

		if (currentLine == null) {
			currentLine = new ArrayList<Segment>();
		}

		if (!SegmentType.TEMPLATE.equals(container.getType())) {
			// Simulate the start tag
			currentLine.add(container);
		}

		for (Segment segment : container) {
			if (segment instanceof ContainerSegment) {
				currentLine = readSegmentLines(lines, currentLine,
						(ContainerSegment) segment);
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
	public static String getSegmentTree(ContainerSegment container) {
		return getSegmentTreeInternal(1, container);
	}

	private static String getSegmentTreeInternal(int level,
			ContainerSegment container) {

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
			if (segment instanceof ContainerSegment) {
				tree.append(getSegmentTreeInternal(level + 1,
						(ContainerSegment) segment));
			} else {
				tree.append(LINE_SEPARATOR);
				tree.append(StringUtils.repeat(" ", level));
				tree.append("-");
				tree.append(segment.getTemplate().getText());
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
