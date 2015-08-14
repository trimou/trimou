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
package org.trimou.jdk8.lambda;

import java.util.function.Function;

import org.trimou.lambda.Lambda;
import org.trimou.lambda.Lambda.InputType;
import org.trimou.lambda.SpecCompliantLambda;
import org.trimou.util.Checker;

/**
 * Allows to create simple {@link Lambda}s using JDK8 funcional interfaces.
 *
 * @author Martin Kouba
 */
public final class SimpleLambdas {

    private SimpleLambdas() {
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @param invokeCallback
     * @return a simple spec compliant lambda instance
     * @see SpecCompliantLambda
     */
    public static Lambda invoke(Function<String, String> invokeCallback) {
        return builder().interpolateReturnValue().inputType(InputType.LITERAL)
                .invoke(invokeCallback).build();
    }

    /**
     * The builder is not thread-safe and should not be reused.
     *
     * @author Martin Kouba
     * @see Lambda
     */
    public static class Builder {

        private InputType inputType;

        private Function<String, String> invokeCallback;

        private boolean isReturnValueInterpolated;

        public Builder interpolateReturnValue() {
            this.isReturnValueInterpolated = true;
            return this;
        }

        public Builder inputType(InputType inputType) {
            this.inputType = inputType;
            return this;
        }

        public Builder invoke(Function<String, String> invokeCallback) {
            this.invokeCallback = invokeCallback;
            return this;
        }

        public Lambda build() {
            return new SimpleLambda(inputType, invokeCallback,
                    isReturnValueInterpolated);
        }

    }

    static class SimpleLambda implements Lambda {

        private final InputType inputType;

        private final Function<String, String> invokeCallback;

        private final boolean isReturnValueInterpolated;

        private SimpleLambda(InputType inputType,
                Function<String, String> invokeCallback,
                boolean isReturnValueInterpolated) {
            Checker.checkArgumentNotNull(inputType);
            Checker.checkArgumentNotNull(invokeCallback);
            this.inputType = inputType;
            this.invokeCallback = invokeCallback;
            this.isReturnValueInterpolated = isReturnValueInterpolated;
        }

        @Override
        public String invoke(String text) {
            return invokeCallback.apply(text);
        }

        @Override
        public InputType getInputType() {
            return inputType;
        }

        @Override
        public boolean isReturnValueInterpolated() {
            return isReturnValueInterpolated;
        }

    }

}
