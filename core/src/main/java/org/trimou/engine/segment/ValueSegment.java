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
import org.trimou.engine.context.ValueWrapper;
import org.trimou.lambda.Lambda;

/**
 * Value segment (aka variable tag).
 *
 * @author Martin Kouba
 */
@Internal
public class ValueSegment extends AbstractSegment implements HelperAwareSegment {

    private final boolean unescape;

    private final HelperExecutionHandler helperHandler;

    /**
     *
     * @param text
     * @param origin
     * @param unescape
     */
    public ValueSegment(String text, Origin origin, boolean unescape) {
        super(text, origin);
        this.unescape = unescape;
        this.helperHandler = isHandlebarsSupportEnabled() ? HelperExecutionHandler.from(
                text, getEngineConfiguration(), this) : null;
    }

    public SegmentType getType() {
        return SegmentType.VALUE;
    }

    public boolean isUnescape() {
        return unescape;
    }

    public void execute(Appendable appendable, ExecutionContext context) {

        if (helperHandler != null) {
            helperHandler.execute(appendable, context);
        } else {

            ValueWrapper value = context.getValue(getText());

            try {

                if (value.isNull()) {
                    Object replacement = getEngineConfiguration()
                            .getMissingValueHandler().handle(getTagInfo());
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
    }

    @Override
    public void fn(Appendable appendable, ExecutionContext context) {
        // No-op
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

    /**
     * Info about {@link ValueSegment}.
     */
    public interface ValueSegmentInfo extends MustacheTagInfo {

        /**
         * @return <code>true</code> if the original value should not be
         *         escaped, <code>false</code> otherwise
         */
        public boolean isUnescape();

    }

}
