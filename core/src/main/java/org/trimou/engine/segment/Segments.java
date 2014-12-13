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

import static org.trimou.engine.config.EngineConfigurationKey.DEBUG_MODE;
import static org.trimou.engine.config.EngineConfigurationKey.TEMPLATE_CACHE_ENABLED;
import static org.trimou.engine.config.EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT;
import static org.trimou.util.Strings.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.parser.Template;

/**
 * {@link Segment} utils.
 *
 * @author Martin Kouba
 */
final class Segments {

    private Segments() {
    }

    /**
     *
     * @param configuration
     * @return <code>true</code> if it's possible to cache the template in a
     *         segment, i.e. if the cache is enabled, no expiration timeout is
     *         set and debug mode is not enabled, <code>false</code> otherwise
     */
    static boolean isTemplateCachingAllowed(Configuration configuration) {
        return !configuration.getBooleanPropertyValue(DEBUG_MODE)
                && configuration
                        .getBooleanPropertyValue(TEMPLATE_CACHE_ENABLED)
                && configuration
                        .getLongPropertyValue(TEMPLATE_CACHE_EXPIRATION_TIMEOUT) <= 0;
    }

    /**
     *
     * @param cachedReference
     * @param templateId
     * @param engine
     * @return the template, use the cache if possible
     */
    static Template getTemplate(AtomicReference<Template> cachedReference,
            String templateId, MustacheEngine engine) {
        if (cachedReference != null) {
            Template template = cachedReference.get();
            if (template == null) {
                synchronized (cachedReference) {
                    if (template == null) {
                        template = (Template) engine.getMustache(templateId);
                        cachedReference.set(template);
                    }
                }
            }
            return template;
        }
        return (Template) engine.getMustache(templateId);
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

        if (!SegmentType.ROOT.equals(container.getType())) {
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

        if (!SegmentType.ROOT.equals(container.getType())) {
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
        if (!SegmentType.ROOT.equals(container.getType())) {
            tree.append(container.getTemplate().getName());
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
