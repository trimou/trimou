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

import java.util.Iterator;
import java.util.List;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;

import com.google.common.collect.ImmutableList;

/**
 * Abstract container segment.
 *
 * @author Martin Kouba
 */
abstract class AbstractContainerSegment extends AbstractSegment implements
        ContainerSegment {

    private final List<Segment> segments;

    /**
     *
     * @param name
     * @param origin
     * @param segments
     */
    public AbstractContainerSegment(String name, Origin origin, List<Segment> segments) {
        super(name, origin);
        this.segments = segments;
    }

    public void execute(Appendable appendable, ExecutionContext context) {
        for (Segment segment : segments) {
            segment.execute(appendable, context);
        }
    }

    @Override
    public Iterator<Segment> iterator() {
        return getSegments().iterator();
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public int getSegmentsSize(boolean recursive) {
        if(recursive) {
            int count = 0;
            for (Segment segment : this) {
                count++;
                if (segment instanceof ContainerSegment) {
                    count += ((ContainerSegment) segment).getSegmentsSize(true);
                }
            }
            return count;
        }
        return segments.size();
    }

    public String getContentLiteralBlock() {
        StringBuilder literal = new StringBuilder();
        for (Segment segment : segments) {
            literal.append(segment.getLiteralBlock());
        }
        return literal.toString();
    }

    @Override
    protected String getSegmentName() {
        return getText();
    }

    @Override
    protected List<MustacheTagInfo> getDirectChildTags() {
        ImmutableList.Builder<MustacheTagInfo> builder = ImmutableList
                .builder();
        for (Segment segment : segments) {
            if (segment.getType().getTagType() != null) {
                builder.add(segment.getTagInfo());
            }
        }
        return builder.build();
    }

}
