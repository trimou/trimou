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
package org.trimou.handlebars;

import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.ConfigurationAware;

/**
 * Handlebars-like helper. Must be thread safe.
 *
 * Member names in this package sometimes do not make much sense (e.g.
 * {@link Options#fn(Appendable)}), however we've tried to follow the original
 * handlebars terminology as much as possible.
 *
 * @author Martin Kouba
 * @see MustacheEngineBuilder#registerHelper(String, Helper)
 * @since 1.5.0
 */
public interface Helper extends ConfigurationAware {

    /**
     *
     * @param appendable
     * @param options
     */
    public void execute(Appendable appendable, Options options);

    /**
     * Helper should validate the tag definition (e.g. number of parameters)
     * during compilation and fail fast if necessary.
     *
     * @param definition
     */
    public void validate(HelperTagDefinition definition);

}
