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

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.handlebars.Options;
import org.trimou.lambda.Lambda.InputType;

/**
 * Compiled template segment. Any segment is considered thread-safe (most often
 * immutable) once the compilation is finished.
 *
 * @author Martin Kouba
 */
@Internal
public interface Segment {

    /**
     * @return the segment type
     */
    public SegmentType getType();

    /**
     * @return the segment text (a tag key, a line separator text, etc.)
     */
    public String getText();

    /**
     * @return the origin of this segment or <code>null</code> in case of this
     *         segment is a template
     */
    public Origin getOrigin();

    /**
     *
     * @return the info about the original tag or <code>null</code> if the
     *         segment does not represent a tag (e.g. {@link SegmentType#TEXT})
     */
    public MustacheTagInfo getTagInfo();

    /**
     * Note that the text is reconstructed and will not be an exact copy when
     * "Set Delimiter" tags are used.
     *
     * @return the reconstructed literal block this segment represents (original
     *         text before compilation)
     * @see InputType#LITERAL
     * @see Options#getContentLiteralBlock()
     */
    public String getLiteralBlock();

    /**
     * Execute this segment (e.g. write down some variable value).
     *
     * @param appendable
     * @param context
     * @return the appendable which should be used for the next executions
     */
    public Appendable execute(Appendable appendable, ExecutionContext context);

}
