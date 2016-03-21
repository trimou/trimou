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
package org.trimou.lambda;

/**
 * Lambda represents a special type of data - a callable object.
 * <p>
 * By default, a lambda is spec compliant. I.e. the input type is
 * {@link InputType#LITERAL} and {@link #isReturnValueInterpolated()} returns
 * <code>true</code>.
 *
 * @author Martin Kouba
 */
@FunctionalInterface
public interface Lambda {

    /**
     * Every time the return value is interpolated a new template is compiled.
     * Its name will have this prefix.
     */
    String ONEOFF_LAMBDA_TEMPLATE_PREFIX = "oneoff_lambda_";

    /**
     * @param text
     *            The section contents (unprocessed or processed - depends on
     *            the input type) in case of section tag, <code>null</code> in
     *            case of variable tag
     * @return the return value
     * @see InputType
     * @see #isReturnValueInterpolated()
     */
    String invoke(String text);

    /**
     * @return the text input type
     */
    default InputType getInputType() {
        return InputType.LITERAL;
    }

    /**
     * @return <code>true</code> if the return value should be parsed and
     *         interpolated, <code>false</code> otherwise
     */
    default boolean isReturnValueInterpolated() {
        return true;
    }

    /**
     * Text input type.
     */
    enum InputType {

        /**
         * The text passed is (almost) the original literal block - complies
         * with the spec.
         */
        LITERAL,
        /**
         * The text passed is processed/rendered before invocation.
         */
        PROCESSED,

    }

}
