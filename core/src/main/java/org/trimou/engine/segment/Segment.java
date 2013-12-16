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
import org.trimou.lambda.Lambda.InputType;

/**
 * Compiled template segment. Any segment is considered immutable once the
 * compilation is finished.
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
     * @return the info about original tag
     */
    public MustacheTagInfo getTagInfo();

    /**
     * @return the reconstructed literal block this segment represents (original
     *         text before compilation)
     * @see InputType#LITERAL
     */
    public String getLiteralBlock();

    /**
     * Execute this segment (e.g. write down some variable value).
     *
     * @param appendable
     * @param context
     */
    public void execute(Appendable appendable, ExecutionContext context);

    /**
     * Perform compilation post processing, e.g. optimization.
     */
    public void performPostProcessing();

}
