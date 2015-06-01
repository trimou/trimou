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
package org.trimou.engine.resolver;

/**
 *
 * @author Martin Kouba
 */
public final class Hints {

    /**
     * A hint which is never applicable. Implementations are encouraged to use
     * this instance if it's not possible to create a hint.
     */
    public static final EnhancedResolver.Hint INAPPLICABLE_HINT = new EnhancedResolver.Hint() {

        @Override
        public Object resolve(Object contextObject, String name,
                ResolutionContext context) {
            return null;
        }
    };

    private Hints() {
    }

}
