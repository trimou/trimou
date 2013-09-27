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
package org.trimou.engine.text;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.StringBuilderWriter;

/**
 *
 * @author Martin Kouba
 */
class DefaultTextSupport extends AbstractConfigurationAware implements
        TextSupport {

    @Override
    public String escapeHtml(String input) {
        final StringBuilderWriter writer = new StringBuilderWriter(input.length());
        try {
            StringEscapeUtils.ESCAPE_HTML3.translate(input, writer);
        } catch (IOException e) {
            throw new MustacheException(MustacheProblem.RENDER_IO_ERROR, e);
        }
        return writer.toString();
    }

}
