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

/**
 * Works similarly as partial tag except the name of the template to include may
 * be obtained dynamically.
 *
 * <code>
 * {{include data.template}}
 * </code>
 *
 * <p>
 * If more than one parameters are specified, the final name to be looked up is
 * made up of concatenated params {@link #toString()} values:
 * </p>
 *
 * <code>
 * {{include "/going/to/be/here/" now}}
 * </code>
 *
 * @author Martin Kouba
 */
public class IncludeHelper extends BasicValueHelper {

    @Override
    public void execute(Options options) {
        String partialName;
        if (options.getParameters().size() == 1) {
            partialName = options.getParameters().get(0).toString();
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object param : options.getParameters()) {
                builder.append(param.toString());
            }
            partialName = builder.toString();
        }
        options.partial(partialName);
    }

}
