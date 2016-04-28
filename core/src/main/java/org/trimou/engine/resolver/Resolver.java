/*
 * Copyright 2013 Martin Kouba
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

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.priority.WithPriority;

/**
 * A value resolver. Implementations must be thread-safe.
 *
 * <p>
 * Any resolver may implement optional interface
 * {@link org.trimou.engine.validation.Validateable}. The validation is
 * performed before a {@link org.trimou.engine.MustacheEngine} is built. An
 * invalid resolver is not put into service, i.e. it's not included in the final
 * list of resolvers returned by
 * {@link org.trimou.engine.config.Configuration#getResolvers()}.
 * </p>
 *
 * @author Martin Kouba
 */
public interface Resolver extends WithPriority, ConfigurationAware {

    int DEFAULT_PRIORITY = 20;

    /**
     * Resolve the value from specified context object and name. This method
     * should return as fast as possible. The best practice is to verify params
     * first and return <code>null</code> in case of the resolver is not capable
     * of resolving it.
     * <p>
     * {@link ResolutionContext} allows to register a callback to release all
     * the relevant resources after the resolved value is used.
     *
     * @param contextObject
     *            The current context object (aka base object), may be
     *            <code>null</code>
     * @param name
     *            The name (the key or its part) is never <code>null</code>
     * @param context
     *            The current resolution context
     * @return the resolved object or <code>null</code>
     * @see Placeholder#NULL
     */
    Object resolve(Object contextObject, String name,
            ResolutionContext context);

    @Override
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

}
