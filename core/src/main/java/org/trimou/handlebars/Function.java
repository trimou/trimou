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

/**
 * Function accepts an {@link Object} argument and produces an {@link Object}
 * result.
 *
 * @see EachHelper
 */
public interface Function {

    /**
     *
     * @param value
     * @return the result
     */
    Object apply(Object value);

}