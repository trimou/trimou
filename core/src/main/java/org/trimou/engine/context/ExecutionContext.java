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
package org.trimou.engine.context;

import org.trimou.annotations.Internal;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.engine.segment.ExtendSegment;

/**
 * A new execution context is created for each template rendering. Execution
 * context is not considered to be thread-safe.
 *
 * @author Martin Kouba
 */
@Internal
public interface ExecutionContext {

    /**
     * @param key
     * @param keyParts
     * @return the wrapper for the given key
     */
    public ValueWrapper getValue(String key, String[] keyParts);

    /**
     * @param key
     * @return the wrapper for the given key
     */
    public ValueWrapper getValue(String key);

    /**
     * Push the object on the specified stack.
     *
     * @param object
     */
    public void push(TargetStack stack, Object object);

    /**
     * Remove the object at the top of the specified stack.
     *
     * @return the removed object
     */
    public Object pop(TargetStack stack);

    /**
     * Returns the object at the top of the context stack.
     *
     * @param stack
     * @return the object at the top of the context stack
     */
    Object peek(TargetStack stack);

    /**
     * Associate the specified defining section with the context, but only if no
     * defining section with the same name is associated.
     *
     * @param name
     * @param segment
     * @see ExtendSegment
     */
    public void addDefiningSection(String name, ExtendSectionSegment segment);

    /**
     * @param name
     * @return the defining section with the specified name or <code>null</code>
     *         if no such associated with the context
     * @see ExtendSegment
     */
    public ExtendSectionSegment getDefiningSection(String name);

    /**
     * Remove all defining sections. This method should be always called after
     * an extend segment is executed.
     */
    public void clearDefiningSections();

    /**
     *
     * @author Martin Kouba
     *
     */
    public enum TargetStack {
        /**
         * Context object stack
         */
        CONTEXT,
        /**
         * Template invocations stack
         */
        TEMPLATE_INVOCATION,
    }

}
