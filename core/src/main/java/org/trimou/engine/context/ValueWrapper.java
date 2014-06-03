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
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resource.AbstractReleaseCallbackContainer;

/**
 * Wrapper for the resolved value object and release callbacks. It is not thread-safe.
 *
 * The {@link #release()} method must be always called after the wrapper is
 * used, even if the resolved object is <code>null</code> (there might be still
 * some callbacks registered).
 *
 * @author Martin Kouba
 */
@Internal
public final class ValueWrapper extends AbstractReleaseCallbackContainer
        implements ResolutionContext {

    private final String key;

    private Object value = null;

    /**
     *
     * @param key
     */
    public ValueWrapper(String key) {
        this.key = key;
    }

    /**
     * @return the resolved object or <code>null</code> if no such object exists
     */
    public Object get() {
        return value;
    }

    /**
     *
     * @param value
     */
    void set(Object value) {
        this.value = value;
    }

    /**
     * @return <code>true</code> if there is no wrapped value (resolved object),
     *         <code>false</code> otherwise
     */
    public boolean isNull() {
        return value == null;
    }

    @Override
    public String getKey() {
        return key;
    }

}
