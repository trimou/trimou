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

import static org.trimou.handlebars.OptionsHashKeys.ELSE;
import static org.trimou.handlebars.OptionsHashKeys.LOGIC;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.util.ImmutableSet;
import org.trimou.util.Strings;

/**
 * An abstract helper which renders a block if:
 * <ul>
 * <li>there are no params and the object at the top of the context stack
 * matches, or</li>
 * <li>the parameters match according to the current evaluation logic</li>
 * </ul>
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
 * <p>
 * Don't make this class public until we have a proper name.
 * </p>
 *
 * @author Martin Kouba
 * @see IfHelper
 * @see UnlessHelper
 */
abstract class MatchingSectionHelper extends BasicSectionHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(MatchingSectionHelper.class);

    private static final Set<String> SUPPORTED_HASH_KEYS = ImmutableSet
            .of(LOGIC, ELSE);

    private final String elseStartDelimiter;

    private final Pattern elsePattern;

    MatchingSectionHelper() {
        this(EngineConfigurationKey.START_DELIMITER.getDefaultValue().toString()
                .substring(0, 1),
                EngineConfigurationKey.END_DELIMITER.getDefaultValue()
                        .toString().substring(0, 1));
    }

    MatchingSectionHelper(String elseStartDelimiter, String elseEndDelimiter) {
        this.elseStartDelimiter = elseStartDelimiter;
        this.elsePattern = initElsePattern(elseStartDelimiter,
                elseEndDelimiter);
    }

    @Override
    public void execute(Options options) {
        if ((options.getParameters().isEmpty() && isMatching(options.peek()))
                || matches(options.getHash(), options.getParameters())) {
            options.fn();
        } else {
            Object elseBlock = getHashValue(options, OptionsHashKeys.ELSE);
            if (elseBlock != null) {
                String elseString = elseBlock.toString();
                if (elseString.contains(elseStartDelimiter)) {
                    append(options, interpolate(elseString, options));
                } else {
                    append(options, elseString);
                }
            }
        }
    }

    @Override
    protected Optional<Set<String>> getSupportedHashKeys() {
        return Optional.of(SUPPORTED_HASH_KEYS);
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
        },;

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
        if (hash.isEmpty() || !hash.containsKey(LOGIC)) {
            return getDefaultLogic();
        }
        String customLogic = hash.get(LOGIC).toString();
        EvaluationLogic logic = EvaluationLogic.parse(customLogic);
        if (logic == null) {
            logger.warn(
                    "Unsupported evaluation logic specified: {}, using the default one: {}",
                    customLogic, getDefaultLogic());
            logic = getDefaultLogic();
        }
        return logic;
    }

    private String interpolate(String elseString, Options options) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = elsePattern.matcher(elseString);
        while (matcher.find()) {
            Object value = options.getValue(matcher.group(2).trim());
            String replacement = value != null ? value.toString()
                    : Strings.EMPTY;
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    static Pattern initElsePattern(String elseStartDelimiter,
            String elseEndDelimiter) {
        return Pattern.compile("(" + Pattern.quote(elseStartDelimiter)
                + ")(.*?)(" + Pattern.quote(elseEndDelimiter) + ")");
    }

}