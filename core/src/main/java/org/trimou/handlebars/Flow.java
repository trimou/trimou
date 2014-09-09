/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.handlebars;

import org.trimou.engine.MustacheTagInfo;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Nested;

/**
 *
 * @author Martin Kouba
 */
public class Flow implements Nested<Object> {

    private static final String MESSAGE_INVALID_FLOW = "Invalid flow context [tag key: %s, template: %s, line: %s]";

    private boolean isTerminated;

    private boolean isFallThrough;

    private final Object parent;

    Flow(Object parent) {
        this.isTerminated = false;
        this.isFallThrough = false;
        this.parent = parent;
    }

    boolean isTerminated() {
        return isTerminated;
    }

    boolean isFallThrough() {
        return isFallThrough;
    }

    void setFallThrough() {
        this.isFallThrough = true;
    }

    void terminate() {
        isTerminated = true;
    }

    public Object up() {
        return parent;
    }

    public static MustacheException newInvalidFlowException(MustacheTagInfo tagInfo) {
        return new MustacheException(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                MESSAGE_INVALID_FLOW, tagInfo.getText(),
                tagInfo.getTemplateName(), tagInfo.getLine());
    }

}