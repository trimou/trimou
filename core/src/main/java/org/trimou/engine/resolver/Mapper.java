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
package org.trimou.engine.resolver;

/**
 * Maps keys to values.
 * <p>
 * {@link MapResolver} handles a mapper in a similar way to
 * {@link java.util.Map}. The difference is that a mapper is not a member of
 * Java Collections Framework and does not have to contain anything. In other
 * words, the lookup may be performed dynamically.
 *
 * @author Martin Kouba
 * @see MapResolver
 * @since 1.6
 */
@FunctionalInterface
public interface Mapper {

    /**
     *
     * @param key
     * @return the value to which the specified key is mapped, or
     *         <code>null</code>
     */
    Object get(String key);

}
