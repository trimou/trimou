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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import org.trimou.annotations.Internal;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ValueWrapper;

/**
 * Inverted section segment.
 *
 * <p>
 * The content is rendered if there is no object found in the context, or is a
 * {@link Boolean} of value <code>false</code>, or is an empty
 * {@link Collection}, or is an {@link Iterable} with no elements, or is an
 * empty array.
 * </p>
 *
 * @author Martin Kouba
 */
@Internal
public class InvertedSectionSegment extends AbstractSectionSegment {

    private final ValueProvider provider;

    public InvertedSectionSegment(String text, Origin origin,
            List<Segment> segments) {
        super(text, origin, segments);
        this.provider = new ValueProvider(text, getEngineConfiguration());
    }

    public SegmentType getType() {
        return SegmentType.INVERTED_SECTION;
    }

    public Appendable execute(Appendable appendable, ExecutionContext context) {
        ValueWrapper value = provider.get(context);
        try {
            if (value.isNull() || process(value.get())) {
                return super.execute(appendable, context);
            } else {
                return appendable;
            }
        } finally {
            value.release();
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean process(Object value) {
        if (value instanceof Boolean) {
            return !(Boolean) value;
        } else if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        } else if (value instanceof Iterable) {
            return !((Iterable) value).iterator().hasNext();
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        return false;
    }

}
