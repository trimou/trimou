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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.parser.DefaultParsingHandler.ContainerSegmentBase;
import org.trimou.engine.parser.DefaultParsingHandler.PartialSegmentBase;
import org.trimou.engine.parser.DefaultParsingHandler.RootSegmentBase;
import org.trimou.engine.parser.DefaultParsingHandler.SegmentBase;
import org.trimou.engine.segment.SegmentType;
import org.trimou.util.Strings;

/**
 * {@link SegmentBase} utils.
 *
 * @author Martin Kouba
 */
final class SegmentBases {

    private static final Logger logger = LoggerFactory
            .getLogger(SegmentBases.class);

    private SegmentBases() {
    }

    static void removeStandaloneLines(RootSegmentBase rootSegment) {

        List<List<SegmentBase>> lines = readSegmentLines(rootSegment);

        // First identify the segments to remove
        Set<SegmentBase> segmentsToRemove = new HashSet<SegmentBase>();
        int idx = 0;
        for (List<SegmentBase> line : lines) {
            idx++;
            if (isStandaloneLine(line)) {

                // Extract indentation for standalone partial segment
                extractIndentationForPartial(line);

                for (SegmentBase segment : line) {
                    if (segment instanceof ContainerSegmentBase
                            || SegmentType.PARTIAL.equals(segment.getType())) {
                        continue;
                    }
                    segmentsToRemove.add(segment);
                }
                logger.trace("Segment line {} is standalone", idx);
            }
        }

        // Then remove segments
        if (!segmentsToRemove.isEmpty()) {
            removeSegments(segmentsToRemove, rootSegment);
            logger.debug("{} segments removed", segmentsToRemove.size());
        }

    }

    static void removeUnnecessarySegments(ContainerSegmentBase container) {

        for (Iterator<SegmentBase> iterator = container.iterator(); iterator
                .hasNext();) {
            SegmentBase segment = iterator.next();
            if (segment instanceof ContainerSegmentBase) {
                removeUnnecessarySegments((ContainerSegmentBase) segment);
            } else if (SegmentType.COMMENT.equals(segment.getType())
                    || SegmentType.DELIMITERS.equals(segment.getType())) {
                iterator.remove();
            }
        }
    }

    static void reuseLineSeparatorSegments(ContainerSegmentBase container) {

        Map<String, SegmentBase> lineSeparators = new HashMap<String, SegmentBase>();

        for (ListIterator<SegmentBase> iterator = container.listIterator(); iterator
                .hasNext();) {

            SegmentBase segment = iterator.next();

            if (segment instanceof ContainerSegmentBase) {
                reuseLineSeparatorSegments((ContainerSegmentBase) segment);
            } else if (SegmentType.LINE_SEPARATOR.equals(segment.getType())) {
                SegmentBase lineSeparator = lineSeparators.get(segment
                        .getContent());
                if (lineSeparator == null) {
                    lineSeparators.put(segment.getContent(), segment);
                } else {
                    iterator.set(lineSeparator);
                }
            }
        }
    }

    static int getNumberOfSegments(ContainerSegmentBase container) {
        int count = 0;
        for (SegmentBase segmentBase : container) {
            if (segmentBase instanceof ContainerSegmentBase) {
                count += getNumberOfSegments((ContainerSegmentBase) segmentBase);
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param standaloneLine
     */
    private static void extractIndentationForPartial(
            List<SegmentBase> standaloneLine) {

        if (SegmentType.TEXT.equals(standaloneLine.get(0).getType())) {

            // First segment is whitespace - try to find partial
            PartialSegmentBase partial = null;
            for (SegmentBase segment : standaloneLine) {
                if (SegmentType.PARTIAL.equals(segment.getType())) {
                    partial = (PartialSegmentBase) segment;
                    break;
                }
            }

            if (partial != null) {
                partial.setIndentation(standaloneLine.get(0).getContent());
            }
        }
    }

    /**
     *
     * @param segmentsToRemove
     * @param container
     */
    private static void removeSegments(Set<SegmentBase> segmentsToRemove,
            ContainerSegmentBase container) {

        for (Iterator<SegmentBase> iterator = container.iterator(); iterator
                .hasNext();) {
            SegmentBase segment = iterator.next();
            if (segment instanceof ContainerSegmentBase) {
                removeSegments(segmentsToRemove, (ContainerSegmentBase) segment);
            } else {
                if (segmentsToRemove.contains(segment)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     *
     * @param line
     * @return
     */
    static boolean isStandaloneLine(List<SegmentBase> line) {

        boolean standaloneCandidate = false;

        for (SegmentBase segment : line) {
            if (SegmentType.VALUE.equals(segment.getType())) {
                // Value segment
                return false;
            } else if (SegmentType.TEXT.equals(segment.getType())) {
                if (!Strings.containsOnlyWhitespace(segment.getContent())) {
                    // Text segment with non-whitespace character
                    return false;
                }
            } else if (isStandaloneCandidate(segment.getType())) {
                standaloneCandidate = true;
            }
        }
        return standaloneCandidate;
    }

    private static boolean isStandaloneCandidate(SegmentType type) {
        return type.equals(SegmentType.COMMENT)
                || type.equals(SegmentType.SECTION)
                || type.equals(SegmentType.INVERTED_SECTION)
                || type.equals(SegmentType.DELIMITERS)
                || type.equals(SegmentType.PARTIAL);
    }

    /**
     * Read segment lines recursively.
     *
     * @param container
     * @return
     */
    private static List<List<SegmentBase>> readSegmentLines(
            ContainerSegmentBase container) {
        List<List<SegmentBase>> lines = new ArrayList<List<SegmentBase>>();
        // Add the last line manually - there is no line separator to trigger
        // flush
        lines.add(readSegmentLines(lines, null, container));
        return lines;
    }

    /**
     *
     * @param lines
     * @param currentLine
     * @param container
     * @return
     */
    private static List<SegmentBase> readSegmentLines(
            List<List<SegmentBase>> lines, List<SegmentBase> currentLine,
            ContainerSegmentBase container) {

        if (currentLine == null) {
            currentLine = new ArrayList<SegmentBase>();
        }

        if (!SegmentType.ROOT.equals(container.getType())) {
            // Simulate the start tag
            currentLine.add(container);
        }

        for (SegmentBase segment : container) {
            if (segment instanceof ContainerSegmentBase) {
                currentLine = readSegmentLines(lines, currentLine,
                        (ContainerSegmentBase) segment);
            } else if (!SegmentType.LINE_SEPARATOR.equals(segment.getType())) {
                currentLine.add(segment);
            } else {
                // New line separator - flush the line
                currentLine.add(segment);
                lines.add(currentLine);
                currentLine = new ArrayList<SegmentBase>();
            }
        }

        if (!SegmentType.ROOT.equals(container.getType())) {
            // Simulate the end tag
            currentLine.add(container);
        }
        return currentLine;
    }

}
