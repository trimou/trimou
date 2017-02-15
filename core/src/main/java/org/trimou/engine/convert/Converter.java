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
package org.trimou.engine.convert;


/**
 * A component which is able to convert an object to a different object.
 *
 * @author Martin Kouba
 *
 * @param <FROM>
 * @param <TO>
 */
@FunctionalInterface
public interface Converter<FROM, TO> {

    /**
     *
     * @param from
     * @return the converted object or <code>null</code> if it's not possible to convert the object
     */
    TO convert(FROM from);

}
