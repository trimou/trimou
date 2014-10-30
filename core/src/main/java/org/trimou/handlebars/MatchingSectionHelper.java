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
 * An abstract helper which renders a block if:
 * <ul>
 * <li>there are no params and the object at the top of the context stack
 * matches, or</li>
 * <li>the parameters match according to the current evaluation logic</li>
 * </ul>
 *
 * Don't make this class public until we have a proper name.
 *
 * @author Martin Kouba
 * @see IfHelper
 * @see UnlessHelper
 */
abstract class MatchingSectionHelper extends BasicSectionHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(MatchingSectionHelper.class);

    private static final String OPTION_KEY_LOGIC = "logic";

    @Override
    public void execute(Options options) {
        if ((options.getParameters().isEmpty() && isMatching(options.peek()))
                || matches(options.getHash(), options.getParameters())) {
            options.fn();
        }
    }

    protected EvaluationLogic getDefaultLogic() {
        return EvaluationLogic.AND;
    }

    protected abstract boolean isMatching(Object value);

    protected boolean hasEmptyParamsSupport() {
        return false;
    }

    @Override
    protected int numberOfRequiredParameters() {
        return hasEmptyParamsSupport() ? 0 : super.numberOfRequiredParameters();
    }

    protected enum EvaluationLogic {

        // At least one of the params must match
        OR {
            Boolean test(boolean isMatching) {
                return isMatching ? true : null;
            }

            boolean defaultResult() {
                return false;
            }
        },
        // All the params must match
        AND {
            Boolean test(boolean isMatching) {
                return !isMatching ? false : null;
            }

            boolean defaultResult() {
                return true;
            }
        },
        ;

        /**
         * @param isParamMatching
         * @return a non-null value if other params do not need to be evaluated,
         *         the return value is the result of the evaluation
         */
        abstract Boolean test(boolean isParamMatching);

        abstract boolean defaultResult();

        public static EvaluationLogic parse(String value) {
            for (EvaluationLogic logic : values()) {
                if (value.equalsIgnoreCase(logic.toString())) {
                    return logic;
                }
            }
            return null;
        }

    }

    private boolean matches(Map<String, Object> hash, List<Object> params) {
        // Very often there is only one param
        if (params.size() == 1) {
            return isMatching(params.get(0));
        }
        EvaluationLogic logic = getLogic(hash);
        for (Object param : params) {
            Boolean value = logic.test(isMatching(param));
            if (value != null) {
                return value;
            }
        }
        return logic.defaultResult();
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