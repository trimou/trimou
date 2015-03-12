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

import org.trimou.annotations.Internal;

/**
 * This is an internal prototype of a new interface which will become part of
 * the public API in the next minor version.
 *
 * @author Martin Kouba
 */
@Internal
public interface EnhancedResolver extends Resolver {

    /**
     *
     * @param contextObject
     * @param name
     * @return the hint or <code>null</code>
     */
    Hint createHint(Object contextObject, String name);

    /**
     * A hint could be used to skip the resolver chain for a simple value
     * reference, e.g. <code>{{foo}}</code>.
     *
     * @author Martin Kouba
     */
    @Internal
    public interface Hint {

        /**
         *
         * @param contextObject
         * @param name
         * @return the resolved object or <code>null</code>
         */
        Object resolve(Object contextObject, String name);

    }

}
