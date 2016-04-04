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
package org.trimou.handlebars;

import org.trimou.util.Checker;

/**
 * Renders a block if the param is not "falsy".
 *
 * <pre>
 * {{#if item.active}}
 *   {{item.name}}
 * {{/if}}
 * </pre>
 *
 * <p>
 * Multiple params may be evaluated. The default evaluation logic is
 * conjunction:
 * </p>
 *
 * <pre>
 * {{#if item.active item.valid}}
 *   Active and valid.
 * {{/if}}
 * </pre>
 *
 * <p>
 * The evaluation logic may be specified:
 * </p>
 *
 * <pre>
 * {{#if item.active item.valid logic="or"}}
 *   Active or valid.
 * {{/if}}
 * </pre>
 *
 * <p>
 * An optional <code>else</code> may be specified. If not a string literal
 * {@link Object#toString()} is used. The final string may contain simple value
 * expressions (evaluated in the same way as helper params). The default
 * delimiters are <code>{</code> and <code>}</code>. Note that for string
 * literals it's not possible to use the current template delimiters, i.e.
 * <code>{{</code> and <code>}}</code>.
 * </p>
 *
 * <pre>
 * {{#if item.active else="{item.name} is not active!"}}
 *   {{item.name}} is active!
 * {{/if}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class IfHelper extends MatchingSectionHelper {

    public IfHelper() {
    }

    /**
     *
     * @param elseStartDelimiter
     * @param elseEndDelimiter
     */
    public IfHelper(String elseStartDelimiter, String elseEndDelimiter) {
        super(elseStartDelimiter, elseEndDelimiter);
    }

    @Override
    protected boolean isMatching(Object value) {
        return !Checker.isFalsy(value);
    }

}
