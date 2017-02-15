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

import org.trimou.Mustache;
import org.trimou.engine.interpolation.KeySplitter;
import org.trimou.exception.MustacheException;

/**
 * This objects represents a helper execution context. A new instance is created
 * for every execution. It is not thread-safe.
 *
 * @author Martin Kouba
 * @since 1.5
 */
public interface Options extends HelperDefinition {

    /**
     * Append the given sequence to the rendered template. The seqence is not
     * modified in any way.
     *
     * @param sequence
     */
    void append(CharSequence sequence);

    /**
     * Proceed with template execution, i.e. execute the block. This is no-op
     * for variable tag helpers.
     */
    void fn();

    /**
     * Render the template with the current context and append the result to the
     * rendered template.
     *
     * @param name
     * @throws MustacheException
     *             If there's no such template
     * @see Mustache#getName()
     */
    void partial(String name);

    /**
     *
     * @param name
     * @return the source of the mustache template
     * @throws MustacheException
     *             If there's no such template
     * @see Mustache#getName()
     * @since 1.6
     */
    String source(String name);

    /**
     * Push the specified object on the context stack. Helper should pop all
     * pushed objects at the end of its execution. Otherwise all remaining
     * objects will be removed automatically.
     *
     * @param contextObject
     */
    void push(Object contextObject);

    /**
     * Removes the object at the top of the context stack and returns that
     * object.
     *
     * @return the object at the top of the context stack
     * @throws MustacheException
     *             In case of a helper tries to pop a context object it did not
     *             push previously
     */
    Object pop();

    /**
     * Returns the object at the top of the context stack without removing it.
     *
     * @return the object at the top of the context stack
     * @since 1.6
     */
    Object peek();

    /**
     * In most cases it's better to use the convenient method
     * {@link #append(CharSequence)} instead.
     *
     * @return The appendable to append the rendered template to
     * @see org.trimou.Mustache#render(Appendable, Object)
     * @since 1.7
     */
    Appendable getAppendable();

    /**
     * Proceed with execution, i.e. execute the block. This is no-op for
     * variable tag helpers.
     *
     * @param appendable
     *            The appendable to append the rendered block to
     * @since 1.7
     */
    void fn(Appendable appendable);

    /**
     * The key is first processed by the {@link KeySplitter} and then processed
     * by the resolver chain.
     *
     * @param key
     * @return the value from the context for the given key, or
     *         <code>null</code> if no such value exists
     * @see KeySplitter
     * @since 1.8
     */
    Object getValue(String key);

    /**
     * Render the template with the current context and append the result to the
     * given appendable.
     *
     * @param templateId
     * @throws MustacheException
     *             If there's no such template
     * @since 1.8
     */
    void partial(String templateId, Appendable appendable);

    /**
     * Executes the given {@link HelperExecutable} asynchronously.
     *
     * @param executable
     * @since 1.8
     */
    void executeAsync(HelperExecutable executable);

    /**
     * A helper task to be executed asynchronously.
     *
     * @author Martin Kouba
     * @since 1.8
     */
    @FunctionalInterface
    interface HelperExecutable {

        /**
         *
         * @param options
         */
        void execute(Options asyncOptions);

    }

}
