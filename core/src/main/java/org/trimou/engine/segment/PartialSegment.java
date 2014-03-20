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

import static org.trimou.engine.context.ExecutionContext.TargetStack.TEMPLATE_INVOCATION;

import java.util.List;

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.parser.Template;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Partial segment.
 *
 * @author Martin Kouba
 */
@Internal
public class PartialSegment extends AbstractSegment {

    private final String indentation;

    public PartialSegment(String text, Origin origin, String indentation) {
        super(text, origin);
        this.indentation = indentation;
    }

    @Override
    public SegmentType getType() {
        return SegmentType.PARTIAL;
    }

    @Override
    public void execute(Appendable appendable, ExecutionContext context) {

        Template partialTemplate = (Template) getEngine()
                .getMustache(getText());

        if (partialTemplate == null) {
            throw new MustacheException(
                    MustacheProblem.RENDER_INVALID_PARTIAL_KEY,
                    "No partial found for the given key: %s %s", getText(),
                    getOrigin());
        }

        context.push(TEMPLATE_INVOCATION, partialTemplate.getRootSegment());
        if (indentation == null) {
            partialTemplate.getRootSegment().execute(appendable, context);
        } else {
            prependIndentation(appendable, context, partialTemplate);
        }
        context.pop(TEMPLATE_INVOCATION);
    }

    @Override
    public String getLiteralBlock() {
        return getTagLiteral(MustacheTagType.PARTIAL.getCommand() + getText());
    }

    @Override
    protected String getSegmentName() {
        return getText();
    }

    private void prependIndentation(Appendable appendable,
            ExecutionContext context, Template partialTemplate) {

        List<List<Segment>> partialLines = Segments
                .readSegmentLinesBeforeRendering(partialTemplate.getRootSegment());
        TextSegment indent = new TextSegment(indentation, new Origin(
                getTemplate()));

        for (List<Segment> line : partialLines) {
            line.add(0, indent);
        }

        for (List<Segment> line : partialLines) {
            for (Segment segment : line) {
                segment.execute(appendable, context);
            }
        }
    }

}
