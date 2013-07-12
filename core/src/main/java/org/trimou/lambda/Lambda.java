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
 *
 * @author Martin Kouba
 */
public interface Lambda {

    /**
     * @param text
     *            The section contents (unprocessed or processed - depends on
     *            the input type) in case of section tag, <code>null</code> in
     *            case of variable tag
     * @return the return value
     * @see InputType
     * @see #isReturnValueInterpolated()
     */
    public String invoke(String text);

    /**
     * @return the text input type
     */
    public InputType getInputType();

    /**
     * @return <code>true</code> if the return value should be parsed and
     *         interpolated, <code>false</code> otherwise
     */
    public boolean isReturnValueInterpolated();

    /**
     *
     */
    public enum InputType {

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
