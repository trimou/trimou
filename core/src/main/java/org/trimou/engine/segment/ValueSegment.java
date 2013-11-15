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
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.context.ValueWrapper;
import org.trimou.engine.interpolation.MissingValueHandler.ValueSegmentInfo;
import org.trimou.lambda.Lambda;

/**
 * Value segment (aka variable tag).
 *
 * @author Martin Kouba
 */
@Internal
public class ValueSegment extends AbstractSegment {

    private final boolean unescape;

    public ValueSegment(String text, Origin origin, boolean unescape) {
        super(text, origin);
        this.unescape = unescape;
    }

    public SegmentType getType() {
        return SegmentType.VALUE;
    }

    public boolean isUnescape() {
        return unescape;
    }

    public void execute(Appendable appendable, ExecutionContext context) {

        ValueWrapper value = context.getValue(getText());

        try {

            if (value.isNull()) {
                Object replacement = getEngineConfiguration()
                        .getMissingValueHandler().handle(getValueSegmentInfo());
                if (replacement != null) {
                    processValue(appendable, context, replacement);
                }
            } else {
                processValue(appendable, context, value.get());
            }

        } finally {
            value.release();
        }
    }

    @Override
    protected String getSegmentName() {
        return getText();
    }

    private void processValue(Appendable appendable, ExecutionContext context,
            Object value) {
        if (value instanceof Lambda) {
            processLambda(appendable, context, value);
        } else {
            writeValue(appendable, value.toString());
        }
    }

    private void writeValue(Appendable appendable, String text) {
        append(appendable, unescape ? text : getEngineConfiguration()
                .getTextSupport().escapeHtml(text));
    }

    private void processLambda(Appendable appendable, ExecutionContext context,
            Object value) {

        Lambda lambda = (Lambda) value;
        String returnValue = lambda.invoke(null);

        if (lambda.isReturnValueInterpolated()) {
            // Parse and interpolate the return value
            StringBuilder interpolated = new StringBuilder();
            TemplateSegment temp = (TemplateSegment) getEngine()
                    .compileMustache(
                            Lambdas.constructLambdaOneoffTemplateName(this),
                            returnValue);
            temp.execute(interpolated, context);
            writeValue(appendable, interpolated.toString());
        } else {
            writeValue(appendable, returnValue);
        }
    }

    private ValueSegmentInfo getValueSegmentInfo() {
        return new ValueSegmentInfo() {

            @Override
            public boolean isUnescape() {
                return unescape;
            }

            @Override
            public String getTemplateName() {
                return getOrigin().getTemplateName();
            }

            @Override
            public int getLine() {
                return getOrigin().getLine();
            }

            @Override
            public String getKey() {
                return getText();
            }
        };
    }

}
