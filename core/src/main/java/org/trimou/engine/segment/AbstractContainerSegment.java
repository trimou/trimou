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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.util.ImmutableList;
import org.trimou.util.ImmutableList.ImmutableListBuilder;

/**
 * Abstract container segment.
 *
 * @author Martin Kouba
 */
abstract class AbstractContainerSegment extends AbstractSegment
        implements ContainerSegment {

    // Sometimes there is only one segment in the container - we use this
    // reference to avoid unnecessary iterations
    private final Segment singleton;

    private final List<Segment> segments;

    /**
     *
     * @param text
     * @param origin
     * @param segments
     */
    protected AbstractContainerSegment(String text, Origin origin,
            List<Segment> segments) {
        super(text, origin);
        this.segments = segments;
        this.singleton = segments.size() == 1 ? segments.get(0) : null;
    }

    public Appendable execute(Appendable appendable, ExecutionContext context) {
        if (singleton != null) {
            appendable = singleton.execute(appendable, context);
        } else {
            for (Segment segment : segments) {
                appendable = segment.execute(appendable, context);
            }
        }
        return appendable;
    }

    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public int getSegmentsSize(boolean recursive) {
        if (recursive) {
            int count = 0;
            for (Segment segment : segments) {
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
        if (singleton != null) {
            return singleton.getLiteralBlock();
        }
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
        if (singleton != null) {
            return Collections.singletonList(singleton.getTagInfo());
        }
        ImmutableListBuilder<MustacheTagInfo> builder = ImmutableList.builder();
        for (Segment segment : segments) {
            if (segment.getType().getTagType() != null) {
                builder.add(segment.getTagInfo());
            }
        }
        return builder.build();
    }

}
