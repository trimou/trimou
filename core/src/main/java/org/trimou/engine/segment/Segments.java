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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
                && configuration.getBooleanPropertyValue(TEMPLATE_CACHE_ENABLED)
                && configuration.getLongPropertyValue(
                        TEMPLATE_CACHE_EXPIRATION_TIMEOUT) <= 0;
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
        return getTemplate(cachedReference, templateId, engine, null);
    }

    /**
     *
     * @param cachedReference
     * @param templateId
     * @param engine
     * @param currentTemplate If set try to lookup nested templates first
     * @return the template, use the cache if possible
     */
    static Template getTemplate(AtomicReference<Template> cachedReference,
            String templateId, MustacheEngine engine, Template currentTemplate) {
        if (cachedReference != null) {
            Template template = cachedReference.get();
            if (template == null) {
                synchronized (cachedReference) {
                    if (template == null) {
                        template = lookupTemplate(templateId, engine, currentTemplate);
                        cachedReference.set(template);
                    }
                }
            }
            return template;
        }
        return lookupTemplate(templateId, engine, currentTemplate);
    }

    /**
     *
     * @param templateId
     * @param engine
     * @param currentTemplate
     * @return the template or <code>null</code>
     */
    public static Template lookupTemplate(String templateId, MustacheEngine engine,
            Template currentTemplate) {
        Template result = null;
        if (currentTemplate != null) {
            result = currentTemplate.getNestedTemplate(templateId);
        }
        if (result == null) {
            result = (Template) engine.getMustache(templateId);
        }
        return result;
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

    static String[] getKeyParts(String text, Configuration configuration) {
        ArrayList<String> parts = new ArrayList<String>();
        for (Iterator<String> iterator = configuration.getKeySplitter()
                .split(text); iterator.hasNext();) {
            parts.add(iterator.next());
        }
        return parts.toArray(new String[parts.size()]);
    }

}
