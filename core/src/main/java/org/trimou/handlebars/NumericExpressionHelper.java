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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiConsumer;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.HelperDefinition.ValuePlaceholder;

/**
 * A simple numeric expression helper. During evaluation all the params are
 * converted to {@link BigDecimal}s. For the list of supported operators see the
 * {@link Operator} enum values, e.g.:
 *
 * <pre>
 * {{numExpr val op="neg" out='It is a negative number!'}}
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
 * <p>
 * It's also possible to specify the default operator ({@link Operator#EQ} by
 * default) so that the <code>op</code> param may be ommitted:
 * </p>
 *
 * <pre>
 * {{#gt val1 10}} val1 > 10 {{/gt}}
 * </pre>
 *
 * <p>
 * Sometimes it also makes sense to register the helper with a name derived from
 * the default operator or even register a helper instance for each operator -
 * see {@link #forEachOperator()}.
 * </p>
 *
 * @author Martin Kouba
 */
public class NumericExpressionHelper extends BasicHelper {

    private final Operator defaultOperator;

    /**
     * {@link Operator#toString()} is used as the helper name.
     *
     * @return a new builder instance with {@link NumericExpressionHelper} instance
     *         registered for each {@link Operator}
     */
    public static HelpersBuilder forEachOperator() {
        return forEachOperator(HelpersBuilder.empty());
    }

    /**
     *
     * @param builder
     * @return an enriched builder instance with {@link NumericExpressionHelper} instance
     *         registered for each {@link Operator}
     */
    public static HelpersBuilder forEachOperator(HelpersBuilder builder) {
        forEachOperator((name, helper) -> builder.add(name, helper));
        return builder;
    }

    /**
     * Invokes the specified consumer for all operators. {@link Operator#toString()}
     * is used as the first argument and the corresponding
     * {@link NumericExpressionHelper} instance as the second argument.
     *
     *
     * @param consumer
     */
    public static void forEachOperator(BiConsumer<String, Helper> consumer) {
        for (Operator operator : Operator.values()) {
            consumer.accept(operator.toString().toLowerCase(), new NumericExpressionHelper(operator));
        }
    }

    /**
     *
     */
    public NumericExpressionHelper() {
        this(Operator.EQ);
    }

    /**
     *
     * @param defaultOperator
     */
    public NumericExpressionHelper(Operator defaultOperator) {
        this.defaultOperator = defaultOperator;
    }

    @Override
    public void execute(Options options) {

        Operator operator = initOperator(options);

        if (operator.getMinParams() > options.getParameters().size()) {
            // We need this check because the operator may be set dynamically
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "More parameters required [helper: %s, template: %s, line: %s]",
                    NumericExpressionHelper.class.getName(),
                    options.getTagInfo().getTemplateName(),
                    options.getTagInfo().getLine());
        }

        boolean result = operator.evaluate(options);

        if (result) {
            if (isSection(options)) {
                options.fn();
            } else {
                String output;
                Object outputValue = options.getHash().get(OUTPUT);
                output = outputValue != null ? convertValue(outputValue)
                        : Boolean.TRUE.toString();
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
        if (operator != null && operator.getMinParams() > definition
                .getParameters().size()) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                    "Operator requires more parameters [helper: %s, template: %s, line: %s]",
                    this.getClass().getName(),
                    definition.getTagInfo().getTemplateName(),
                    definition.getTagInfo().getLine());
        }
    }

    private Operator initOperator(Options options) {
        Operator operator = null;
        Object value = options.getHash().get(OPERATOR);
        if (value != null) {
            operator = Operator.from(value.toString());
        }
        return operator != null ? operator : defaultOperator;
    }

    private static BigDecimal getDecimal(int index, Options options) {
        return getDecimal(options.getParameters().get(index), options);
    }

    static BigDecimal getDecimal(Object value, Options options) {
        BigDecimal decimal;
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
                    value, NumericExpressionHelper.class.getName(),
                    options.getTagInfo().getTemplateName(),
                    options.getTagInfo().getLine());
        }
        return decimal;
    }

    /**
     *
     * @author Martin Kouba
     */
    public enum Operator {

        /**
         * Evaluates to true if the first and the second value are equal in
         * value.
         *
         * @see BigDecimal#compareTo(BigDecimal)
         */
        EQ(new EqualsEvaluator()),
        /**
         * Evaluates to true if the first and the second value are NOT equal in
         * value.
         *
         * @see BigDecimal#compareTo(BigDecimal)
         */
        NEQ(new InverseEvaluator(new EqualsEvaluator())),
        /**
         * Evaluates to true if the first value is greater than the second
         * value.
         *
         * @see BigDecimal#compareTo(BigDecimal)
         */
        GT(options -> {
            BigDecimal val1 = getDecimal(0, options);
            BigDecimal val2 = getDecimal(1, options);
            return val1.compareTo(val2) > 0;
        }),
        /**
         * Evaluates to true if the first value is greater than or equal to the
         * second value.
         *
         * @see BigDecimal#compareTo(BigDecimal)
         */
        GE(options -> {
            BigDecimal val1 = getDecimal(0, options);
            BigDecimal val2 = getDecimal(1, options);
            return val1.compareTo(val2) >= 0;
        }),
        /**
         * Evaluates to true if the first value is less than the second value.
         *
         * @see BigDecimal#compareTo(BigDecimal)
         */
        LT(options -> {
            BigDecimal val1 = getDecimal(0, options);
            BigDecimal val2 = getDecimal(1, options);
            return val1.compareTo(val2) < 0;
        }),
        /**
         * Evaluates to true if the first value is less than or equal to the
         * second value.
         *
         * @see BigDecimal#compareTo(BigDecimal)
         */
        LE(options -> {
            BigDecimal val1 = getDecimal(0, options);
            BigDecimal val2 = getDecimal(1, options);
            return val1.compareTo(val2) <= 0;
        }),
        /**
         * Evaluates to true if the first value is negative.
         */
        NEG(1, options -> getDecimal(0, options).compareTo(BigDecimal.ZERO) < 0),
        /**
         * Evaluates to true if the first value is positive.
         */
        POS(1, options -> getDecimal(0, options).compareTo(BigDecimal.ZERO) > 0),
        /**
         * Evaluates to true if the first value is found in the set of other
         * values. Elements of {@link Iterable}s and arrays are treated as
         * separate objects.
         */
        IN(new InEvaluator()),
        /**
         * Evaluates to true if the first value is not found in the set of other
         * values. Elements of {@link Iterable}s and arrays are treated as
         * separate objects.
         */
        NIN(new InverseEvaluator(new InEvaluator())),;

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
            if (value != null) {
                for (Operator operator : values()) {
                    if (operator.toString().equalsIgnoreCase(value)) {
                        return operator;
                    }
                }
            }
            return null;
        }

    }

    interface Evaluator {

        boolean evaluate(Options options);
    }

    private static final class InEvaluator implements Evaluator {

        @Override
        public boolean evaluate(Options options) {
            BigDecimal val = getDecimal(0, options);
            for (int i = 1; i < options.getParameters().size(); i++) {
                Object toTest = options.getParameters().get(i);
                if (toTest == null) {
                    continue;
                }
                if (toTest instanceof Iterable) {
                    for (final Object o : ((Iterable<?>) toTest)) {
                        if (test(val, getDecimal(o, options))) {
                            return true;
                        }
                    }
                } else if (toTest.getClass().isArray()) {
                    int length = Array.getLength(toTest);
                    for (int j = 0; j < length; j++) {
                        if (test(val,
                                getDecimal(Array.get(toTest, j), options))) {
                            return true;
                        }
                    }
                } else {
                    if (test(val, getDecimal(toTest, options))) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean test(BigDecimal val1, BigDecimal val2) {
            return val1.compareTo(val2) == 0;
        }

    }

    private static class EqualsEvaluator implements Evaluator {

        @Override
        public boolean evaluate(Options options) {
            BigDecimal val1 = getDecimal(0, options);
            BigDecimal val2 = getDecimal(1, options);
            return val1.compareTo(val2) == 0;
        }

    }

    private static class InverseEvaluator implements Evaluator {

        private final Evaluator evaluator;

        InverseEvaluator(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        @Override
        public boolean evaluate(Options options) {
            return !evaluator.evaluate(options);
        }

    }

}
