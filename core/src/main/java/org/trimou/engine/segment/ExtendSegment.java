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
import java.util.concurrent.atomic.AtomicReference;

import org.trimou.annotations.Internal;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.parser.Template;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * This segment extends some template and overrides its extending sections.
 *
 * <pre>
 * {{&lt;super}} {{$insert}}Foo{{/insert}} {{/super}}
 * </pre>
 *
 * @author Martin Kouba
 */
@Internal
public class ExtendSegment extends AbstractSectionSegment {

    /**
     * Cache the partial template if possible, i.e. if the cache is enabled, no
     * expiration timeout is set and debug mode is not enabled
     */
    private final AtomicReference<Template> cachedExtendedTemplate;

    public ExtendSegment(String text, Origin origin, List<Segment> segments) {
        super(text, origin, segments);
        this.cachedExtendedTemplate = Segments
                .isTemplateCachingAllowed(getEngineConfiguration()) ? new AtomicReference<Template>()
                : null;
    }

    @Override
    public SegmentType getType() {
        return SegmentType.EXTEND;
    }

    @Override
    public Appendable execute(Appendable appendable, ExecutionContext context) {

        Template extended = Segments.getTemplate(cachedExtendedTemplate,
                getText(), getEngine());

        if (extended == null) {
            throw new MustacheException(
                    MustacheProblem.RENDER_INVALID_EXTEND_KEY,
                    "No template to extend found for the given key: %s %s",
                    getText(), getOrigin());
        }
        return extended.getRootSegment().execute(appendable, context.setDefiningSections(this));
    }

}
