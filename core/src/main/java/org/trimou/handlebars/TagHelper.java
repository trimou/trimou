/*
 * Copyright 2017 Trimou team
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

import static org.trimou.util.Strings.GAP;
import static org.trimou.util.Strings.HASH;
import static org.trimou.util.Strings.SLASH;

import java.util.Iterator;

import org.trimou.engine.config.EngineConfigurationKey;

/**
 * This helper allows to easily render tags which make use of the same
 * delimiters as Trimou does (i.e. would be normally parsed as mustache tags).
 * The first parameter represents the tag name (needed to render section tags
 * correctly). Other parameters (optional) are simply evaluated and added as tag
 * params (the first one is separated by a space). For example:
 *
 * <pre>
 * {{tag "foo"}}
 * </pre>
 *
 * will be rendered as:
 *
 * <pre>
 * {{foo}}
 * </pre>
 *
 * A little bit more complicated example:
 *
 * <pre>
 * {{#tag "each" "items as='item'"}}
 *   {{tag "this"}}
 * {{/tag}}
 * </pre>
 *
 * will be rendered as:
 *
 * <pre>
 * {{#each items as='item'}}
 *   {{this}}
 * {{/each}}
 * </pre>
 *
 * @see EngineConfigurationKey#START_DELIMITER
 * @see EngineConfigurationKey#END_DELIMITER
 * @author Martin Kouba
 * @since 2.2.1
 */
public class TagHelper extends BasicHelper {

    @Override
    public void execute(Options options) {
        String tag = options.getParameters().get(0).toString();
        if (isSection(options)) {
            options.append(startDelimiter());
            options.append(HASH);
            options.append(tag);
            appendParams(options);
            options.append(endDelimiter());
            options.fn();
            options.append(startDelimiter());
            options.append(SLASH);
            options.append(tag);
            options.append(endDelimiter());
        } else {
            options.append(startDelimiter());
            options.append(tag);
            appendParams(options);
            options.append(endDelimiter());
        }
    }

    private void appendParams(Options options) {
        if (options.getParameters().size() > 1) {
            options.append(GAP);
            for (Iterator<Object> iterator = options.getParameters().listIterator(1); iterator.hasNext();) {
                options.append(iterator.next().toString());
            }
        }
    }

    private String startDelimiter() {
        return configuration.getStringPropertyValue(EngineConfigurationKey.START_DELIMITER);
    }

    private String endDelimiter() {
        return configuration.getStringPropertyValue(EngineConfigurationKey.END_DELIMITER);
    }

}
