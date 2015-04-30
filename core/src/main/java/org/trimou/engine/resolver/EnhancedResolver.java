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

import org.trimou.engine.config.EngineConfigurationKey;

/**
 * An enhanced resolver should be able to create a {@link Hint} for a
 * sucessfully resolved context object and name.
 *
 * @author Martin Kouba
 * @see EngineConfigurationKey#RESOLVER_HINTS_ENABLED
 */
public interface EnhancedResolver extends Resolver {

    /**
     * This method may only be called right after the
     * {@link #resolve(Object, String, ResolutionContext)} of the same resolver
     * returns a non-null value. The parameters must be the same.
     *
     * <p>
     * The created hint is currently only used to skip the resolver chain for
     * the first part of a key of a variable tag, i.e. <code>foo</code> for
     * <code>{{foo}}</code> or <code>{{foo.bar}}</code>.
     * </p>
     *
     * @param contextObject
     * @param name
     * @return the hint or <code>null</code> if it's not possible to create one
     */
    Hint createHint(Object contextObject, String name);

    /**
     * A hint could be used to skip the resolver chain for a specific context
     * object and name (the key or its part). The hint can only be used for the
     * same variable tag. However, the runtime class of the context object may
     * be different than the class used to create the hint.
     *
     * <p>
     * Implementations must be thread-safe.
     * </p>
     *
     * @author Martin Kouba
     * @see EnhancedResolver#createHint(Object, String)
     */
    public interface Hint {

        /**
         * Note that the runtime class of the context object may be different
         * than the class used to create the hint.
         *
         * <p>
         * If <code>null</code> is returned, the resolver chain will be used to
         * resolve the value.
         * </p>
         *
         * @param contextObject
         *            The current context object, may be <code>null</code>
         * @param name
         *            The name (the key or its part) is never <code>null</code>
         * @return the resolved object or <code>null</code>
         */
        Object resolve(Object contextObject, String name);

    }

}
