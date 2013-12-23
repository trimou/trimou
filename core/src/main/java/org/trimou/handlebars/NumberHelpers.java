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

import org.trimou.engine.MustacheTagType;

/**
 *
 * @author Martin Kouba
 * @since 1.5.0
 */
public final class NumberHelpers {

    public static final IsEvenHelper IS_EVEN_HELPER = new IsEvenHelper();

    /**
     * TODO comment
     *
     * {{isEven iterIndex "evenRow"}}
     *
     * @author Martin Kouba
     */
    public static class IsEvenHelper extends AbstractValueHelper {

        @Override
        public void validate(HelperTagDefinition definition) {
            HelperValidator.checkType(definition, MustacheTagType.VARIABLE,
                    MustacheTagType.UNESCAPE_VARIABLE);
            HelperValidator.checkParams(definition, 2);
        }

        @Override
        public void execute(Options options) {

            Object param = options.getParameters().get(0);

            if (param instanceof Number && isEven((Number) param)) {
                options.append(options.getParameters().get(1).toString());
            }
        }

    }

    private static boolean isEven(final Number number) {
        return number.intValue() % 2 == 0;
    }

}
