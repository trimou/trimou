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
package org.trimou.el;

import static org.trimou.el.Expressions.eval;

import java.util.Collections;
import java.util.Set;

import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.handlebars.BasicHelper;
import org.trimou.handlebars.Options;

/**
 * Evaluates the {@link Object#toString()} of the first parameter. If the helper
 * represents a section and the value is not null the value is pushed on the
 * context stack and the section is rendered. If the helper represents a
 * variable and the value is null, the current {@link MissingValueHandler} is
 * used. If the helper represents a variable and the final value is not null the
 * the value's {@link Object#toString()} is rendered.
 *
 * <pre>
 * {{el 'item.active ? "active" : "inactive"'}}
 * </pre>
 *
 * <pre>
 * {{#el "item1.price > item2.price ? item1 : item2'}}
 *  Name of item with higher price: {{name}}
 * {{/el}}
 * </pre>
 *
 * @author Martin Kouba
 * @since 2.0
 */
public class ELHelper extends BasicHelper {

    public static final String DEFAULT_NAME = "el";

    @Override
    public void execute(Options options) {
        Object value = eval(options.getParameters().get(0).toString(), options, configuration);
        if (isSection(options)) {
            if (value != null) {
                options.push(value);
                options.fn();
                options.pop();
            }
        } else {
            if (value == null) {
                value = configuration.getMissingValueHandler()
                        .handle(options.getTagInfo());
            }
            if (value != null) {
                append(options, value.toString());
            }
        }
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ELProcessorFactory.EL_PROCESSOR_FACTORY_KEY);
    }

}
