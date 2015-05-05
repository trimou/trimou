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
package org.trimou.handlebars;

import org.trimou.handlebars.Options.HelperExecutable;

/**
 * A simple helper whose content is rendered asynchronously.
 *
 * <code>
 * {{#async}}
 *  This will be rendered asynchronously!
 * {{/async}}
 * </code>
 *
 * @author Martin Kouba
 */
public class AsyncHelper extends BasicSectionHelper {

    @Override
    public void execute(Options options) {
        options.executeAsync(new HelperExecutable() {
            @Override
            public void execute(Options asyncOptions) {
                asyncOptions.fn();
            }
        });
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

}
