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
package org.trimou.engine;

import java.util.List;

import org.trimou.Mustache;

/**
 * Info about a tag.
 *
 * @author Martin Kouba
 * @since 1.5
 */
public interface MustacheTagInfo {

    /**
     *
     * @return the type
     */
    MustacheTagType getType();

    /**
     * @return the text (e.g. variable key)
     */
    String getText();

    /**
     * @return the original line where the segment comes from
     */
    int getLine();

    /**
     * @return the template name
     * @see Mustache#getName()
     */
    String getTemplateName();

    /**
     *
     * @return a generated template identifier, unique accross the {@link MustacheEngine}
     * @see Mustache#getGeneratedId()
     * @since 2.0
     */
    Long getTemplateGeneratedId();

    /**
     *
     * @return a tag identifier, unique for the template
     * @since 2.0
     */
    String getId();

    /**
     *
     * @return an immutable list of direct child tags, or an empty list for
     *         non-block tags
     * @since 1.7
     */
    List<MustacheTagInfo> getChildTags();

}