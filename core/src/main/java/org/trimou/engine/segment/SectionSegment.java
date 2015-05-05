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
import java.util.Iterator;
import java.util.List;

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ValueWrapper;
import org.trimou.engine.parser.Template;
import org.trimou.handlebars.HelperValidator;
import org.trimou.handlebars.Options;
import org.trimou.lambda.Lambda;

/**
 * Section segment.
 *
 * <p>
 * The content is not rendered if there is no object found in the context, or
 * the found object is:
 * </p>
 * <ul>
 * <li>a {@link Boolean} of value <code>false</code>,</li>
 * <li>an {@link Iterable} with no elements,</li>
 * <li>an empty array.</li>
 * </ul>
 *
 * <p>
 * The content is rendered one or more times if there is an object found in the
 * context. If the found object is:
 * </p>
 * <ul>
 * <li>non-empty {@link Iterable} or array, the content is rendered for each
 * element,</li>
 * <li>a {@link Boolean} of value <code>true</code>, the content is rendered
 * once,</li>
 * <li>an instance of {@link Lambda}, the content is processed according to the
 * lambda's specification,</li>
 * <li>any other kind of object represents a nested context.</li>
 * </ul>
 *
 * @author Martin Kouba
 * @see Lambda
 * @see InvertedSectionSegment
 */
@Internal
public class SectionSegment extends AbstractSectionSegment implements
        HelperAwareSegment {

    private final HelperExecutionHandler helperHandler;

    public SectionSegment(String text, Origin origin, List<Segment> segments) {
        super(text, origin, segments);
        this.helperHandler = isHandlebarsSupportEnabled() ? HelperExecutionHandler
                .from(text, getEngine(), this) : null;
    }

    public SegmentType getType() {
        return SegmentType.SECTION;
    }

    public void execute(Appendable appendable, ExecutionContext context) {
        if (helperHandler != null) {
            helperHandler.execute(appendable, context);
        } else {
            ValueWrapper value = context.getValue(getText());
            try {
                if (value.isNull()) {
                    return;
                }
                processValue(appendable, context, value.get());
            } finally {
                value.release();
            }
        }
    }

    /**
     *
     * @param appendable
     * @param context
     * @see Options#fn()
     */
    public void fn(Appendable appendable, ExecutionContext context) {
        super.execute(appendable, context);
    }

    @Override
    public String getLiteralBlock() {
        StringBuilder literal = new StringBuilder();
        literal.append(getTagLiteral(getType().getTagType().getCommand()
                + getText()));
        literal.append(getContentLiteralBlock());
        if (helperHandler != null) {
            literal.append(getTagLiteral(MustacheTagType.SECTION_END
                    .getCommand()
                    + HelperValidator.splitHelperName(getText(), this).next()));
        } else {
            literal.append(getTagLiteral(MustacheTagType.SECTION_END
                    .getCommand() + getText()));
        }
        return literal.toString();
    }

    private void processValue(Appendable appendable, ExecutionContext context,
            Object value) {
        if (value instanceof Boolean) {
            // Boolean#TRUE, true
            if ((Boolean) value) {
                super.execute(appendable, context);
            }
        } else if (value instanceof Iterable) {
            // Iterable
            processIterable(appendable, context, value);
        } else if (value.getClass().isArray()) {
            // Array
            processArray(appendable, context, value);
        } else if (value instanceof Lambda) {
            // Lambda
            processLambda(appendable, context, value);
        } else {
            // Nested context
            super.execute(appendable, context.setContextObject(value));
        }
    }

    @SuppressWarnings("rawtypes")
    private void processIterable(Appendable appendable,
            ExecutionContext context, Object iterable) {

        Iterator iterator = ((Iterable) iterable).iterator();

        if (!iterator.hasNext()) {
            return;
        }

        IterationMeta meta = new IterationMeta(getEngineConfiguration()
                .getStringPropertyValue(
                        EngineConfigurationKey.ITERATION_METADATA_ALIAS),
                iterator);
        context = context.setContextObject(meta);
        while (iterator.hasNext()) {
            processIteration(appendable, context,
                    iterator.next(), meta);
        }
    }

    private void processArray(Appendable appendable, ExecutionContext context,
            Object array) {

        int length = Array.getLength(array);

        if (length < 1) {
            return;
        }

        IterationMeta meta = new IterationMeta(getEngineConfiguration()
                .getStringPropertyValue(
                        EngineConfigurationKey.ITERATION_METADATA_ALIAS),
                length);
        context = context.setContextObject(meta);
        for (int i = 0; i < length; i++) {
            processIteration(appendable, context, Array.get(array, i), meta);
        }
    }

    private void processIteration(Appendable appendable,
            ExecutionContext context, Object value, IterationMeta meta) {
        super.execute(appendable, context.setContextObject(value));
        meta.nextIteration();
    }

    private void processLambda(Appendable appendable, ExecutionContext context,
            Object value) {

        Lambda lambda = (Lambda) value;

        String input;
        switch (lambda.getInputType()) {
        case LITERAL:
            // Try to reconstruct the original text
            input = getContentLiteralBlock();
            break;
        case PROCESSED:
            StringBuilder processed = new StringBuilder();
            super.execute(processed, context);
            input = processed.toString();
            break;
        default:
            throw new IllegalStateException("Unsupported lambda input type");
        }

        String returnValue = lambda.invoke(input);

        if (lambda.isReturnValueInterpolated()) {
            // Parse and interpolate the return value
            Template temp = (Template) getEngine().compileMustache(
                    Lambdas.constructLambdaOneoffTemplateName(this),
                    returnValue);
            temp.getRootSegment().execute(appendable, context);
        } else {
            append(appendable, returnValue);
        }
    }

}
