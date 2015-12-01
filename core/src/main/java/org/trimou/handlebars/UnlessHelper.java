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
 * Renders a block if the param is "falsy".
 *
 * <pre>
 * {{#unless item.active}}
 *   Not active.
 * {{/unless}}
 * </pre>
 *
 * <p>
 * Multiple params may be evaluated. The default evaluation logic is
 * disjunction:
 * </p>
 *
 * <pre>
 * {{#unless item.active item.valid}}
 *   Not active or not valid.
 * {{/unless}}
 * </pre>
 *
 * <p>
 * The evaluation logic may be specified:
 * </p>
 *
 * <pre>
 * {{#unless item.active item.valid logic="and"}}
 *   Nor active nor valid.
 * {{/unless}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class UnlessHelper extends MatchingSectionHelper {

    /**
     *
     */
    public UnlessHelper() {
        super();
    }

    /**
     *
     * @param elseStartDelimiter
     * @param elseEndDelimiter
     */
    public UnlessHelper(String elseStartDelimiter, String elseEndDelimiter) {
        super(elseStartDelimiter, elseEndDelimiter);
    }

    @Override
    protected boolean isMatching(Object value) {
        return Checker.isFalsy(value);
    }

    @Override
    protected EvaluationLogic getDefaultLogic() {
        return EvaluationLogic.OR;
    }

}
