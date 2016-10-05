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
package org.trimou.engine.validation;

/**
 * Represents a component which is able to validate itself. Not all component
 * types support validation. The specific components define the time when is the
 * validation performed and what happens with an invalid component.
 *
 * @author Martin Kouba
 * @since 1.6
 */
public interface Validateable {

    /**
     *
     * @return true if valid, false otherwise
     */
    default boolean isValid() {
        return true;
    }

}
