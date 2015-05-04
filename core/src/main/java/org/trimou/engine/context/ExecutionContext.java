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

import java.util.concurrent.atomic.AtomicReference;

import org.trimou.annotations.Internal;
import org.trimou.engine.parser.Template;
import org.trimou.engine.resolver.EnhancedResolver.Hint;
import org.trimou.engine.segment.ExtendSegment;
import org.trimou.engine.segment.Segment;

/**
 * The execution context is implemented as a hierarchy of immutable objects.
 * Each modification results in a new child context whose parent represents the
 * state before the modification. The child context does not copy the entire
 * state. Instead, it's delegating to parent in some cases.
 *
 * @author Martin Kouba
 */
@Internal
public interface ExecutionContext {

    /**
     * @param key
     * @param keyParts
     * @param hintRef
     * @return the wrapper for the given key
     */
    ValueWrapper getValue(String key, String[] keyParts,
            AtomicReference<Hint> hintRef);

    /**
     * @param key
     * @return the wrapper for the given key
     */
    ValueWrapper getValue(String key);

    /**
     *
     * @param object
     * @return a new child execution context
     */
    ExecutionContext setContextObject(Object object);

    /**
     *
     * @return the first non-null context object (walking up the hierarchy if
     *         needed), or <code>null</code> if not found
     */
    Object getFirstContextObject();

    /**
     *
     * @param template
     * @return a new child execution context
     */
    ExecutionContext setTemplateInvocation(Template template);

    /**
     * Associate the given defining sections with the context, but only if no
     * defining section with the same name is already associated.
     *
     * @param segments
     * @return a new child execution context
     * @see ExtendSegment
     */
    ExecutionContext setDefiningSections(Iterable<Segment> segments);

    /**
     * @param name
     * @return the defining section with the specified name or <code>null</code>
     *         if no such associated with the context
     * @see ExtendSegment
     */
    Segment getDefiningSection(String name);

    /**
     *
     * @return the parent execution context or <code>null</code>
     */
    ExecutionContext getParent();

}
