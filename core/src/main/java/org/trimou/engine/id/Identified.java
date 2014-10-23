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
package org.trimou.engine.id;

/**
 * Any component that needs to be identified by a generated {@link Long} id.
 *
 * @author Martin Kouba
 * @see IdentifierGenerator
 * @since 1.7
 */
public interface Identified {

    /**
     * The id must be unique for the given component type and
     * {@link org.trimou.engine.MustacheEngine} instance.
     *
     * @return the generated id
     */
    Long getGeneratedId();

}
