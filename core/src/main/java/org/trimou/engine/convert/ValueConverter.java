/*
 * Copyright 2016 Martin Kouba
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

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.validation.Validateable;

/**
 * A value converter is used to convert an object to a string representation.
 * <p>
 * Any converter may validate itself - see also {@link Validateable}. The
 * validation is performed before a {@link org.trimou.engine.MustacheEngine} is
 * built. An invalid converter is not put into service, i.e. it's not included
 * in the final list of converters returned by
 * {@link org.trimou.engine.config.Configuration#getValueConverters()}.
 * <p>
 * This component has also a priority - converters with higher priority are
 * called first, and is {@link ConfigurationAware}.
 * <p>
 * If no component is able to convert an object, {@link Object#toString()} is
 * used.
 *
 * @author Martin Kouba
 */
@FunctionalInterface
public interface ValueConverter extends Converter<Object, String>, WithPriority,
        Validateable, ConfigurationAware {

}
