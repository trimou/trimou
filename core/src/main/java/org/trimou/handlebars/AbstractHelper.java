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
package org.trimou.handlebars;

import java.io.IOException;

import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.text.TextSupport;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public abstract class AbstractHelper extends AbstractConfigurationAware
        implements Helper {

    private volatile TextSupport textSupport;

    @Override
    public void validate(HelperDefinition definition) {
        // No-op by default
    }

    @Override
    protected void init() {
        if (!configuration
                .getBooleanPropertyValue(EngineConfigurationKey.SKIP_VALUE_ESCAPING)) {
            textSupport = configuration.getTextSupport();
        }
    }

    protected Object getHashValue(Options options, String key) {
        return options.getHash().isEmpty() ? null : options.getHash().get(key);
    }

    protected boolean isSection(Options options) {
        return options.getTagInfo().getType().equals(MustacheTagType.SECTION);
    }

    protected boolean isVariable(Options options) {
        return options.getTagInfo().getType().equals(MustacheTagType.VARIABLE)
                || options.getTagInfo().getType()
                        .equals(MustacheTagType.UNESCAPE_VARIABLE);
    }

    protected boolean isUnescapeVariable(Options options) {
        return options.getTagInfo().getType()
                .equals(MustacheTagType.UNESCAPE_VARIABLE);
    }

    /**
     * Escape appended sequence if needed.
     *
     * @param options
     * @param sequence
     * @see TextSupport
     */
    protected void append(Options options, CharSequence sequence) {
        if (textSupport == null || isUnescapeVariable(options)) {
            options.append(sequence);
        } else {
            try {
                textSupport.appendEscapedHtml(sequence.toString(),
                        options.getAppendable());
            } catch (IOException e) {
                throw new MustacheException(MustacheProblem.RENDER_IO_ERROR, e);
            }
        }
    }

}
