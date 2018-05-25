/*
 * Copyright 2018 Trimou team
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

import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.validation.Validateable;

/**
 * This converter can be used to convert context objects before they are
 * processed by resolvers.
 *
 * <p>
 * Any converter may validate itself - see also {@link Validateable}. The
 * validation is performed before a {@link org.trimou.engine.MustacheEngine} is
 * built. An invalid converter is not put into service, i.e. it's not included
 * in the final list of converters returned by
 * {@link org.trimou.engine.config.Configuration#getContextConverters()}.
 * </p>
 *
 * <p>
 * This component has also priority - converters with higher priority are called
 * first. When a converter is able to convert the value all other converters
 * with lower priority are skipped.
 * </p>
 *
 * @author Martin Kouba
 * @since 2.5
 * @see Resolver#resolve(Object, String,
 *      org.trimou.engine.resolver.ResolutionContext)
 * @see MustacheEngineBuilder#addContextConverter(ContextConverter)
 */
@FunctionalInterface
public interface ContextConverter extends Converter<Object, Object>, WithPriority, Validateable, ConfigurationAware {

}
