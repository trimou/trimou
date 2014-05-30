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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract helper which renders a block if the parameters match. Don't make
 * this class public until we have a proper name.
 *
 * @author Martin Kouba
 * @see IfHelper
 * @see UnlessHelper
 */
abstract class ParamMatchingSectionHelper extends BasicSectionHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(ParamMatchingSectionHelper.class);

    private static final String OPTION_KEY_LOGIC = "logic";

    @Override
    public void execute(Options options) {
        if (matches(getLogic(options.getHash()), options.getParameters())) {
            options.fn();
        }
    }

    protected EvaluationLogic getDefaultLogic() {
        return EvaluationLogic.AND;
    }

    protected abstract boolean isMatching(Object value);

    private boolean matches(EvaluationLogic logic, List<Object> params) {
        switch (logic) {
        case AND:
            // All the params must match
            for (Object param : params) {
                if (!isMatching(param)) {
                    return false;
                }
            }
            return true;
        case OR:
            // At least one of the params must match
            for (Object param : params) {
                if (isMatching(param)) {
                    return true;
                }
            }
            return false;
        default:
            throw new IllegalStateException();
        }
    }

    protected enum EvaluationLogic {

        OR,
        AND, ;

        public static EvaluationLogic parse(String value) {
            for (EvaluationLogic logic : values()) {
                if (value.equalsIgnoreCase(logic.toString())) {
                    return logic;
                }
            }
            return null;
        }
    }

    private EvaluationLogic getLogic(Map<String, Object> hash) {
        if (hash.isEmpty() || !hash.containsKey(OPTION_KEY_LOGIC)) {
            return getDefaultLogic();
        }
        String customLogic = hash.get(OPTION_KEY_LOGIC).toString();
        EvaluationLogic logic = EvaluationLogic.parse(customLogic);
        if (logic == null) {
            logger.warn(
                    "Unsupported evaluation logic specified: {}, using the default one: {}",
                    customLogic, getDefaultLogic());
            logic = getDefaultLogic();
        }
        return logic;
    }

}