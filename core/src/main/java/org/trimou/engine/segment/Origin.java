/*
 * Copyright 2013 Trimou team
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

import org.trimou.annotations.Internal;
import org.trimou.engine.parser.Template;
import org.trimou.util.Strings;

/**
 * A segment origin.
 *
 * @author Martin Kouba
 */
@Internal
public class Origin {

    private final Template template;

    /**
     * The original line where the segment comes from (we cannot calculate this
     * because of "remove standalone lines" spec feature)
     */
    private final Integer line;

    /**
     * An index within the template (segments are parsed sequentially)
     */
    private final Integer index;

    /**
     * An artificial segment.
     *
     * @param template
     */
    public Origin(Template template) {
        this.template = template;
        this.line = null;
        this.index = null;
    }

    /**
     *
     * @param template
     * @param line
     * @param index
     */
    public Origin(Template template, int line, int index) {
        this.template = template;
        this.line = line;
        this.index = index;
    }

    public Template getTemplate() {
        return template;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getIndex() {
        return index;
    }

    public String getTemplateName() {
        return template.getName();
    }

    @Override
    public String toString() {
        return String.format("[template: %s, line: %s, idx: %s]", template
                .getName(), line != null ? line : Strings.NOT_AVAILABLE,
                index != null ? index : Strings.NOT_AVAILABLE);
    }

}
