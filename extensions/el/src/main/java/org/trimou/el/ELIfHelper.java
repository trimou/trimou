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

import org.trimou.handlebars.IfHelper;
import org.trimou.handlebars.Options;

/**
 * Extends {@link IfHelper} in the sense that a {@link String} param is
 * evaluated as EL expression:
 *
 * <pre>
 * {{#if "item.price gt 200"}}
 *   {{item.name}}
 * {{/if}}
 * </pre>
 *
 * @author Martin Kouba
 * @since 2.0
 */
public class ELIfHelper extends IfHelper {

    @Override
    protected boolean isMatching(Object value, Options options) {
        if (value instanceof String) {
            // String is expected to be an EL expression
            return super.isMatching(eval(value.toString(), options));
        } else {
            return super.isMatching(value);
        }
    }

}
