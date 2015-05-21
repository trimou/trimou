/*
 * Copyright 2015 Martin Kouba
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

import static org.trimou.handlebars.OptionsHashKeys.OPERATOR;
import static org.trimou.handlebars.OptionsHashKeys.OUTPUT;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.HelperDefinition.ValuePlaceholder;

/**
 * A simple numeric expression helper. During evaluation all the params are
 * converted to {@link BigDecimal}s.
 *
 * <pre>
 * {{numExpr val op="neg" out='It's a negative number!'}}
 * {{#numExpr val "90" op="gt"}}
 *  val > 90
 * {{/numExpr}}
 * {{#numExpr val 10 op="eq"}}
 *  val == 10
 * {{/numExpr}}
 * {{#numExpr val1 val2 op="le"}}
 *  val1 <= val2
 * {{/numExpr}}
 * {{#numExpr val "1" 2 '3' op="in"}}
 *  val = 1 or val = 2 or val = 3
 * {{/numExpr}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class NumericExpressionHelper extends BasicHelper {

    @Override
    public void execute(Options options) {

        Operator operator = initOperator(options);

        if (operator.getMinParams() > options.getParameters().size()) {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "More parameters required [helper: %s, template: %s, line: %s]",
                    NumericExpressionHelper.class.getName(), options
                            .getTagInfo().getTemplateName(), options
                            .getTagInfo().getLine());
        }

        boolean result = operator.evaluate(options);

        if (result) {
            if (isSection(options)) {
                options.fn();
            } else {
                String output;
                Object outputValue = getHashValue(options, OUTPUT);
                output = outputValue != null ? outputValue.toString() : "true";
                append(options, output);
            }
        }
    }

    @Override
    public void validate(HelperDefinition definition) {
        super.validate(definition);
        Operator operator;
        Object value = definition.getHash().get(OPERATOR);
        if (value == null) {
            operator = Operator.EQ;
        } else if (value instanceof ValuePlaceholder) {
            // Operator set dynamically
            operator = null;
        } else {
            operator = Operator.from(value.toString());
        }
        if (operator != null
                && operator.getMinParams() > definition.getParameters().size()) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                    "Operator requires more parameters [helper: %s, template: %s, line: %s]",
                    this.getClass().getName(), definition.getTagInfo()
                            .getTemplateName(), definition.getTagInfo()
                            .getLine());
        }
    }

    private Operator initOperator(Options options) {
        Object value = getHashValue(options, OPERATOR);
        if (value != null) {
            return Operator.from(value.toString());
        }
        return Operator.EQ;
    }

    private static BigDecimal getDecimal(int index, Options options) {
        BigDecimal decimal;
        Object value = options.getParameters().get(index);
        if (value instanceof BigDecimal) {
            decimal = (BigDecimal) value;
        } else if (value instanceof BigInteger) {
            decimal = new BigDecimal((BigInteger) value);
        } else if (value instanceof Long) {
            decimal = new BigDecimal((Long) value);
        } else if (value instanceof Integer) {
            decimal = new BigDecimal((Integer) value);
        } else if (value instanceof Double) {
            decimal = new BigDecimal((Double) value);
        } else if (value instanceof String) {
            decimal = new BigDecimal(value.toString());
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "Parameter is not valid [param: %s, helper: %s, template: %s, line: %s]",
                    value, NumericExpressionHelper.class.getName(), options
                            .getTagInfo().getTemplateName(), options
                            .getTagInfo().getLine());
        }
        return decimal;
    }

    /**
     *
     * @author Martin Kouba
     */
    static enum Operator {

        EQ(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val1 = getDecimal(0, options);
                BigDecimal val2 = getDecimal(1, options);
                return val1.compareTo(val2) == 0;
            }
        }),
        NEQ(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val1 = getDecimal(0, options);
                BigDecimal val2 = getDecimal(1, options);
                return val1.compareTo(val2) != 0;
            }
        }),
        GT(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val1 = getDecimal(0, options);
                BigDecimal val2 = getDecimal(1, options);
                return val1.compareTo(val2) > 0;
            }
        }),
        GE(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val1 = getDecimal(0, options);
                BigDecimal val2 = getDecimal(1, options);
                return val1.compareTo(val2) >= 0;
            }
        }),
        LT(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val1 = getDecimal(0, options);
                BigDecimal val2 = getDecimal(1, options);
                return val1.compareTo(val2) < 0;
            }
        }),
        LE(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val1 = getDecimal(0, options);
                BigDecimal val2 = getDecimal(1, options);
                return val1.compareTo(val2) <= 0;
            }
        }),
        NEG(1, new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                return getDecimal(0, options).compareTo(BigDecimal.ZERO) < 0;
            }
        }),
        POS(1, new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                return getDecimal(0, options).compareTo(BigDecimal.ZERO) > 0;
            }
        }),
        IN(new Evaluator() {
            @Override
            public boolean evaluate(Options options) {
                BigDecimal val = getDecimal(0, options);
                Set<BigDecimal> decimals = new HashSet<BigDecimal>();
                for (int i = 1; i < options.getParameters().size(); i++) {
                    decimals.add(getDecimal(i, options));
                }
                for (BigDecimal decimal : decimals) {
                    if (decimal.compareTo(val) == 0) {
                        return true;
                    }
                }
                return false;
            }
        }),

        ;

        Operator(Evaluator evaluator) {
            this(2, evaluator);
        }

        Operator(int minParams, Evaluator evaluator) {
            this.minParams = minParams;
            this.evaluator = evaluator;
        }

        private final int minParams;

        private final Evaluator evaluator;

        public int getMinParams() {
            return minParams;
        }

        public boolean evaluate(Options options) {
            return evaluator.evaluate(options);
        }

        static Operator from(String value) {
            for (Operator operator : values()) {
                if (operator.toString().equalsIgnoreCase(value)) {
                    return operator;
                }
            }
            return null;
        }

    }

    static interface Evaluator {

        boolean evaluate(Options options);
    }

}
