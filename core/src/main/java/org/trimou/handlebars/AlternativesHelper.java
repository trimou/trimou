/*
 * Copyright 2016 Martin Kouba
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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.interpolation.MissingValueHandler;

/**
 * Selects one object from the set of alternatives (usually helper parameters)
 * and:
 * <ul>
 * <li>for variable - renders its {@link #toString()} value or if no such object
 * exists renders the output of
 * {@link MissingValueHandler#handle(org.trimou.engine.MustacheTagInfo)}</li>
 * <li>for section - pushes the object on the context stack or does nothing if
 * no such object exists</li>
 * </ul>
 *
 * <p>
 * If no {@link Selector} instance is declared the default behavior is: take the
 * first parameter matching the condition (by default not null or not empty for
 * an instance of {@link String}).
 * </p>
 *
 * <p>
 * In some situations a more appropriate name could be used - the user is free
 * to register this helper with any name - see also
 * {@link MustacheEngineBuilder#registerHelper(String, Helper)}.
 * </p>
 *
 * <p>
 * It's useful to specify default values:
 * </p>
 *
 * <code>
 * Username: {{alt username "Joe"}}
 * </code>
 *
 * <p>
 * The number of parameters is not limited:
 * </p>
 *
 * <code>
 * Call me {{alt user.name user.nick "Joe"}}!
 * </code>
 *
 * <p>
 * A custom {@link Selector} might be used to change the default behavior. See
 * for example {@link MinSelector} and {@link #min()}:
 * </p>
 *
 * <code>
 * {{min item1.price item2.price}}
 * </code>
 *
 *
 * @author Martin Kouba
 * @see HelpersBuilder#addAlt()
 */
public class AlternativesHelper extends BasicHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(AlternativesHelper.class);

    private final Selector selector;

    /**
     * Constructs the default version of the helper using
     * {@link ConditionSelector} with the default condition.
     */
    public AlternativesHelper() {
        this(new ConditionSelector());
    }

    /**
     *
     * @param selector
     */
    public AlternativesHelper(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void execute(Options options) {
        Object value = selector.select(options);
        if (isSection(options)) {
            if (value != null) {
                options.push(value);
                options.fn();
                options.pop();
            }
        } else {
            if (value == null) {
                value = configuration.getMissingValueHandler()
                        .handle(options.getTagInfo());
            }
            if (value != null) {
                append(options, value.toString());
            }
        }
    }

    @Override
    public void validate(HelperDefinition definition) {
        super.validate(definition);
        if (definition.getParameters().size() == 1) {
            logger.warn(
                    "{} is not really useful for single parameter [template: {}, line: {}]",
                    getClass().getSimpleName(),
                    definition.getTagInfo().getTemplateName(),
                    definition.getTagInfo().getLine());
        }
    }

    /**
     * Selects the first parameter matching the condition (by default not null
     * or not empty for an instance of {@link String}).
     */
    public static class ConditionSelector implements Selector {

        private final Condition condition;

        public ConditionSelector() {
            this(new Condition() {
                @Override
                public boolean matches(Object param) {
                    if (param == null) {
                        return false;
                    }
                    if (param instanceof String) {
                        return !param.toString().isEmpty();
                    }
                    return true;
                }
            });
        }

        /**
         *
         * @param condition
         */
        public ConditionSelector(Condition condition) {
            this.condition = condition;
        }

        @Override
        public Object select(Options options) {
            for (Object param : options.getParameters()) {
                if (condition.matches(param)) {
                    return param;
                }
            }
            return null;
        }

        /**
         * TODO
         */
        public interface Condition {

            boolean matches(Object param);

        }

    }

    /**
     * Attempts to select the minimal value from the parameters. All parameters
     * must be {@link BigDecimal}, {@link BigInteger} , {@link Long},
     * {@link Integer}, {@link Double} or a string representation of a
     * {@code BigDecimal}.
     *
     * @author Martin Kouba
     *
     */
    public static class MinSelector implements Selector {

        public static final String DEFAULT_NAME = "min";

        @Override
        public Object select(Options options) {
            BigDecimal min = null;
            Object selected = null;
            for (Object param : options.getParameters()) {
                BigDecimal paramDecimal = NumericExpressionHelper
                        .getDecimal(param, options);
                if (min == null || paramDecimal.compareTo(min) < 0) {
                    min = paramDecimal;
                    selected = param;
                }
            }
            return selected;
        }

    }

    /**
     * Attempts to select the maximal value from the parameters. All parameters
     * must be {@link BigDecimal}, {@link BigInteger} , {@link Long},
     * {@link Integer}, {@link Double} or a string representation of a
     * {@code BigDecimal}.
     *
     * @author Martin Kouba
     *
     */
    public static class MaxSelector implements Selector {

        public static final String DEFAULT_NAME = "max";

        @Override
        public Object select(Options options) {
            BigDecimal max = null;
            Object selected = null;
            for (Object param : options.getParameters()) {
                BigDecimal paramDecimal = NumericExpressionHelper
                        .getDecimal(param, options);
                if (max == null || paramDecimal.compareTo(max) > 0) {
                    max = paramDecimal;
                    selected = param;
                }
            }
            return selected;
        }

    }

    /**
     * Selects one object from the set of alternatives.
     */
    public interface Selector {

        Object select(Options options);

    }

}
