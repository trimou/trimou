/*
 * Copyright 2017 Martin Kouba
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

import java.util.Collections;
import java.util.Set;

import org.trimou.engine.config.ConfigurationKey;
import org.trimou.handlebars.EachHelper;
import org.trimou.handlebars.Options;

/**
 * Extends {@link EachHelper} in the sense that a {@link String} param is
 * evaluated as EL expression:
 *
 * <pre>
 * {{#each "[1,2,3]"}}
 *   {{.}}
 * {{/each}}
 * </pre>
 *
 * @author Martin Kouba
 * @since 2.4
 */
public class ELEachHelper extends EachHelper {

    @Override
    protected int processParameter(Object param, Options options, int index, int size, boolean isOmitMeta) {
        if (param instanceof String) {
            // String is expected to be an EL expression
            return super.processParameter(Expressions.eval(param.toString(), options, configuration), options, index,
                    size, isOmitMeta);
        }
        return super.processParameter(param, options, index, size, isOmitMeta);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ELProcessorFactory.EL_PROCESSOR_FACTORY_KEY);
    }

}
