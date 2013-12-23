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

import org.trimou.engine.MustacheTagInfo;

/**
 *
 * @author Martin Kouba
 * @since 1.5.0
 */
public interface HelperTagDefinition extends HelperArguments {

    /**
     *
     * @return the FQCN of the helper
     */
    String getHelperClassName();

    /**
     *
     * @return the info about the associated tag
     */
    MustacheTagInfo getTagInfo();

    /**
     * A value placeholder represents an expression which will be evaluated
     * right before the helper execution. The placeholder is then replaced with
     * the actual value.
     */
    public interface ValuePlaceholder {

        public String getName();

    }

}
