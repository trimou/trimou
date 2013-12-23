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

import org.trimou.exception.MustacheException;

/**
 * A new instance is created for every helper execution.
 *
 * @author Martin Kouba
 * @since 1.5.0
 */
public interface Options extends HelperArguments {

    /**
     * Append the given sequence to the rendered template.
     *
     * @param sequence
     */
    void append(CharSequence sequence);

    /**
     * Proceed with template execution (e.g. execute the block).
     */
    void fn();

    /**
     * Push the specified object on the context stack.
     *
     * @param contextObject
     */
    void push(Object contextObject);

    /**
     * @return the object at the top of the context stack
     * @throws MustacheException
     *             In case of a helper tries to pop a context object it did not
     *             push previously
     */
    Object pop();

}
