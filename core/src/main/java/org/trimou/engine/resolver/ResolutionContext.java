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
package org.trimou.engine.resolver;

import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.resource.ReleaseCallbackContainer;
import org.trimou.engine.segment.Segment;

/**
 * Value resolution context is initialized for each
 * {@link ExecutionContext#getValue(String)} call and destroyed once the
 * template {@link Segment} which asks for a value is executed.
 *
 * Release callbacks are invoked right before the context is destroyed.
 *
 * @author Martin Kouba
 */
public interface ResolutionContext extends ReleaseCallbackContainer {

    /**
     *
     * @return the variable tag key
     */
    String getKey();

    /**
     *
     * @return the index of the part of the key which is currently processed
     * @since 1.6
     */
    int getKeyPartIndex();

}
