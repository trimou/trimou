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
import java.util.Collections;
import java.util.List;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.parser.Template;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;
import org.trimou.util.Strings;

/**
 * Abstract template segment.
 *
 * @author Martin Kouba
 */
abstract class AbstractSegment implements Segment {

    private final Origin origin;

    private final String text;

    private final MustacheTagInfo info;

    /**
     *
     * @param text
     * @param origin
     */
    public AbstractSegment(String text, Origin origin) {
        Checker.checkArgumentsNotNull(text, origin);
        this.text = text;
        this.origin = origin;
        if (getType().getTagType() == null) {
            this.info = null;
        } else {
            this.info = new DefaultTagInfo();
        }
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
        return info;
    }

    @Override
    public String getLiteralBlock() {
        return getTagLiteral(getText());
    }

    @Override
    public String toString() {
        return String.format("%s:%s %s", getType(), getSegmentName(),
                getOrigin());
    }

    public Template getTemplate() {
        return origin.getTemplate();
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

    protected MustacheTagType getTagType() {
        return getType().getTagType();
    }

    protected List<MustacheTagInfo> getDirectChildTags() {
        return Collections.emptyList();
    }

    class DefaultTagInfo implements MustacheTagInfo {

        @Override
        public MustacheTagType getType() {
            return getTagType();
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public int getLine() {
            return origin.getLine();
        }

        @Override
        public String getTemplateName() {
            return origin.getTemplateName();
        }

        @Override
        public Long getTemplateGeneratedId() {
            return origin.getTemplate().getGeneratedId();
        }

        @Override
        public String getId() {
            return origin.getIndex() != null ? origin.getIndex().toString()
                    : "" + origin.hashCode();
        }

        @Override
        public List<MustacheTagInfo> getChildTags() {
            return getDirectChildTags();
        }

        @Override
        public String toString() {
            return String.format("%s:%s %s", getType(), getText(), getOrigin());
        }

    }

}
