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

import static org.trimou.engine.config.EngineConfigurationKey.END_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.START_DELIMITER;

import java.io.IOException;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

/**
 * Abstract template segment.
 *
 * @author Martin Kouba
 */
abstract class AbstractSegment implements Segment {

    private final Origin origin;

    private final String text;

    /**
     *
     * @param text
     * @param template
     */
    public AbstractSegment(String text, Origin origin) {
        super();
        this.text = text;
        this.origin = origin;
    }

    public String getText() {
        return text;
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    @Override
    public MustacheTagInfo getTagInfo() {
        return new DefaultSegmentInfo(getType().getTagType(), getText(),
                getOrigin().getLine(), getOrigin().getTemplateName());
    }

    @Override
    public String getLiteralBlock() {
        return getTagLiteral(getText());
    }

    @Override
    public void performPostProcessing() {
        // No-op by default
    }

    @Override
    public String toString() {
        return String.format("%s:%s %s", getType(), getSegmentName(),
                getOrigin());
    }

    public TemplateSegment getTemplate() {
        return origin != null ? origin.getTemplate() : null;
    }

    protected boolean isReadOnly() {
        return getTemplate().isReadOnly();
    }

    protected MustacheEngine getEngine() {
        return getTemplate().getEngine();
    }

    protected Configuration getEngineConfiguration() {
        return getEngine().getConfiguration();
    }

    protected String getDefaultStartDelimiter() {
        return getEngineConfiguration().getStringPropertyValue(START_DELIMITER);
    }

    protected String getDefaultEndDelimiter() {
        return getEngineConfiguration().getStringPropertyValue(END_DELIMITER);
    }

    protected String getTagLiteral(String content) {
        return getDefaultStartDelimiter() + content + getDefaultEndDelimiter();
    }

    protected boolean isHandlebarsSupportEnabled() {
        return getEngineConfiguration().getBooleanPropertyValue(
                EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED);
    }

    /**
     *
     * @return the segment name
     */
    protected String getSegmentName() {
        return Strings.EMPTY;
    }

    protected void append(Appendable appendable, String text) {
        try {
            appendable.append(text);
        } catch (IOException e) {
            throw new MustacheException(MustacheProblem.RENDER_IO_ERROR, e);
        }
    }

    protected void checkModificationAllowed() {
        if (isReadOnly()) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED,
                    toString());
        }
    }

    class DefaultSegmentInfo implements MustacheTagInfo {

        private final MustacheTagType type;

        private final String text;

        private final int line;

        private final String templateName;

        /**
         *
         * @param type
         * @param text
         * @param line
         * @param templateName
         */
        public DefaultSegmentInfo(MustacheTagType type, String text, int line,
                String templateName) {
            super();
            this.type = type;
            this.text = text;
            this.line = line;
            this.templateName = templateName;
        }

        @Override
        public MustacheTagType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public int getLine() {
            return line;
        }

        @Override
        public String getTemplateName() {
            return templateName;
        }

    }

}
