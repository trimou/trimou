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

import java.util.Map;

import org.trimou.util.Nested;

/**
 * This helper works similarly as the Java switch statement.
 *
 * <p>
 * At the moment it's not possible to validate the helper content properly. It
 * should only contain <b>case</b> and <b>default</b> sections. Other types of
 * segments are always rendered. Note that the <b>default</b> section always
 * terminates the flow!
 * </p>
 *
 * <p>
 * Since we push the flow object on the context stack, it's possible to refer
 * the value in the switch expression with {@link Nested#up()} method. See also examples.
 * </p>
 *
 * By default, the following template will render "Hello world!":
 *
 * <pre>
 * {{#switch "hello"}}
 *   {{#case "foo" break="true"}}
 *      Hello foo!
 *   {{/case}}
 *   {{#case "hello" break="true"}}
 *      Hello world!
 *   {{/case}}
 *   {{#default}}
 *      No case for {{this.up}}.
 *   {{/default}}
 * {{/switch}}
 * </pre>
 *
 * By default, the following template will render "Hello world! No case for hello.":
 *
 * <pre>
 * {{#switch "hello"}}
 *   {{#case "foo"}}
 *      Hello foo!
 *   {{/case}}
 *   {{#case "hello"}}
 *      Hello world!
 *   {{/case}}
 *   {{#default}}
 *      No case for {{this.up}}.
 *   {{/default}}
 * {{/switch}}
 * </pre>
 *
 *
 * @author Martin Kouba
 * @see HelpersBuilder#addSwitch()
 */
public class SwitchHelper extends BasicSectionHelper {

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    @Override
    public void execute(Options options) {
        // Take the first param or the current context object
        Object value = options.getParameters().isEmpty() ? options.peek()
                : options.getParameters().get(0);
        if (value == null) {
            // Don't match a null value
            return;
        }
        options.push(new Flow(value));
        options.fn();
        options.pop();
    }

    /**
     * The first param is the matching value. By default, the flow is only terminated if a key <code>break</code> has the value of true.
     *
     * @author Martin Kouba
     */
    public static class CaseHelper extends BasicSectionHelper {

        private static final String OPTION_BREAK = "break";

        private final boolean defaultIsBreak;

        /**
         *
         */
        public CaseHelper() {
            this(false);
        }

        /**
         *
         * @param defaultIsBreak
         *            If <code>true</code> the matching case helper terminates the flow
         *            by default.
         */
        public CaseHelper(boolean defaultIsBreak) {
            this.defaultIsBreak = defaultIsBreak;
        }

        @Override
        public void execute(Options options) {
            Object contextObject = options.peek();
            if (contextObject instanceof Flow) {
                Flow flow = (Flow) contextObject;
                if (!flow.isTerminated()) {
                    if (flow.isFallThrough()
                            || flow.up().equals(options.getParameters().get(0))) {
                        options.fn();
                        flow.setFallThrough();
                        if (isBreak(options.getHash())) {
                            flow.terminate();
                        }
                    }
                }
            } else {
                throw Flow.newInvalidFlowException(options.getTagInfo());
            }
        }

        private boolean isBreak(Map<String, Object> hash) {
            if (hash.isEmpty() || !hash.containsKey(OPTION_BREAK)) {
                return defaultIsBreak;
            }
            return Boolean.valueOf(hash.get(OPTION_BREAK).toString());
        }

    }

    public static class DefaultHelper extends BasicSectionHelper {

        @Override
        protected int numberOfRequiredParameters() {
            return 0;
        }

        @Override
        public void execute(Options options) {
            Object contextObject = options.peek();
            if (contextObject instanceof Flow) {
                Flow flow = (Flow) contextObject;
                if (!flow.isTerminated()) {
                    options.fn();
                    // Always terminate the flow
                    flow.terminate();
                }
            } else {
                throw Flow.newInvalidFlowException(options.getTagInfo());
            }
        }

    }

}
