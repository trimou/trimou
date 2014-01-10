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

import org.trimou.util.Checker;

/**
 * Renders the second param if the first param is not falsy, or (optionally,
 * i.e. if set) the third param. Note that params do not have to be string
 * literals.
 *
 * <code>
 * {{is item.active "active"}}
 * </code>
 *
 * <code>
 * {{is item.active "active" "notActive"}}
 * </code>
 *
 * @author Martin Kouba
 * @see Checker#isFalsy(Object)
 * @since 1.5
 */
public class IsHelper extends BasicValueHelper {

    @Override
    protected int numberOfRequiredParameters() {
        return 2;
    }

    @Override
    public void execute(Options options) {
        if (!Checker.isFalsy(options.getParameters().get(0))) {
            options.append(options.getParameters().get(1).toString());
        } else {
            if (options.getParameters().size() > 2) {
                options.append(options.getParameters().get(2).toString());
            }
        }
    }

}
