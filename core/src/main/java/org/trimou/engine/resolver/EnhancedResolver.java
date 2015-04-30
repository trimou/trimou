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
 * <p>
 * Note that hints may not always give better results. E.g. if we have a tag
 * <code>{{foo.bar}}</code> and first we pass data where foo is resolved as a
 * map and then another data where foo is resolved as an integer, the hint
 * created for the map will not work for integer. In this case, the hint should
 * inform that it's not applicable and the resolver chain will be used to
 * resolve the value.
 * </p>
 *
 * <p>
 * {@link EngineConfigurationKey#RESOLVER_HINTS_ENABLED} can be used to disable
 * the hints entirely.
 * </p>
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
     * <p>
     * This method must not return <code>null</code>.
     * </p>
     *
     * @param contextObject
     * @param name
     * @return the hint
     * @see #INAPPLICABLE_HINT
     */
    Hint createHint(Object contextObject, String name);

    /**
     * A hint could be used to skip the resolver chain for a part of the key of
     * a specific tag. The hint can only be used for the same tag for which the
     * context object and name were resolved. However, the resolver is permitted
     * to reuse a hint instance.
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
         * If a hint is not applicable it should return <code>null</code> and
         * the resolver chain will be used to resolve the value.
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

    /**
     * A hint which is never applicable. Implementations are encouraged to use
     * this instance if it's not possible to create a hint.
     */
    public static Hint INAPPLICABLE_HINT = new Hint() {

        @Override
        public Object resolve(Object contextObject, String name) {
            return null;
        }
    };

}
