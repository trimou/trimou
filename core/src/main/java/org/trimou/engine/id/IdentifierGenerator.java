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

import org.trimou.engine.config.ConfigurationAware;

/**
 * An idenfitier (long value) generator. Implementations must be thread-safe.
 * There are some restrictions on the uniqueness of the generated id - see
 * {@link #generate(Class)}.
 *
 * @author Martin Kouba
 * @since 1.7
 */
public interface IdentifierGenerator extends ConfigurationAware {

    /**
     * If the <code>identified</code> parameter is <code>null</code>, the
     * returned value must be unique per the
     * {@link org.trimou.engine.MustacheEngine} instance. Otherwise, the value
     * must be unique for the given component type and
     * {@link org.trimou.engine.MustacheEngine} instance, i.e. it may also be
     * unique per the {@link org.trimou.engine.MustacheEngine} instance.
     *
     * @param componentType
     *            The type of a component the identifier is generated for, may
     *            be <code>null</code>
     * @return the generated identifier
     */
    public long generate(Class<? extends Identified> componentType);

}
