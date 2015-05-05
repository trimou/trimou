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

import java.util.List;

import org.trimou.annotations.Internal;
import org.trimou.engine.context.ExecutionContext;

/**
 * The root segment of a template.
 *
 * @author Martin Kouba
 */
@Internal
public class RootSegment extends AbstractContainerSegment {

    public RootSegment(Origin origin, List<Segment> segments) {
        super(SegmentType.ROOT.toString(), origin, segments);
    }

    @Override
    public SegmentType getType() {
        return SegmentType.ROOT;
    }

    @Override
    public String getLiteralBlock() {
        return getContentLiteralBlock();
    }

    @Override
    public Appendable execute(Appendable appendable, ExecutionContext context) {
        return super.execute(appendable,
                context.setTemplateInvocation(getTemplate()));
    }

    public static void flushAsyncAppendable(Appendable appendable) {
        AsyncAppendable.flushIfNeeded(appendable);
    }

}
