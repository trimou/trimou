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

import org.trimou.util.Checker;
import org.trimou.util.Nested;

/**
 * This helper works similarly as the JSP c:choose tag. It renders the content
 * of the first <b>when</b> section whose first param is not falsy. If no
 * <b>when</b> section is rendered, <b>otherwise</b> section is rendered, if
 * present.
 *
 * <p>
 * At the moment it's not possible to validate the helper content properly.
 * However, it should only contain <b>when</b> and <b>otherwise</b> sections.
 * Other types of segments are always rendered.
 * </p>
 *
 * <p>
 * Since we push the flow object on the context stack, it's possible to refer
 * the last context object with {@link Nested#up()} method. See also examples.
 * </p>
 *
 * The following template:
 *
 * <pre>
 * {{#choose}}
 *   {{#when this.up}}
 *      Hello active!
 *   {{/when}}
 *   {{#when foo}}
 *      Hello foo!
 *   {{/when}}
 *   {{#otherwise}}
 *      No match.
 *   {{/otherwise}}
 * {{/switch}}
 * </pre>
 *
 * will render "Hello active!" if last context object (i.e. "this" before we enter
 * the choose helper) is not falsy. "Hello foo" if "this.up" is falsy and "foo"
 * is not falsy. And "No match." if both "this.up" and "foo" are falsy.
 *
 * @author Martin Kouba
 */
public class ChooseHelper extends BasicSectionHelper {

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    @Override
    public void execute(Options options) {
        options.push(new Flow(options.peek()));
        options.fn();
        options.pop();
    }

    /**
     * The first param is the test condition.
     *
     * @author Martin Kouba
     */
    public static class WhenHelper extends BasicSectionHelper {

        @Override
        public void execute(Options options) {
            Object contextObject = options.peek();
            if (contextObject instanceof Flow) {
                Flow flow = (Flow) contextObject;
                if (!flow.isTerminated()
                        && !Checker.isFalsy(options.getParameters().get(0))) {
                    options.fn();
                    flow.terminate();
                }
            } else {
                throw Flow.newInvalidFlowException(options.getTagInfo());
            }
        }

    }

    public static class OtherwiseHelper extends BasicSectionHelper {

        @Override
        protected int numberOfRequiredParameters() {
            return 0;
        }

        @Override
        public void execute(Options options) {
            Object conditionObject = options.peek();
            if (conditionObject instanceof Flow) {
                Flow condition = (Flow) conditionObject;
                if (!condition.isTerminated()) {
                    options.fn();
                    condition.terminate();
                }
            } else {
                throw Flow.newInvalidFlowException(options.getTagInfo());
            }
        }

    }

}
