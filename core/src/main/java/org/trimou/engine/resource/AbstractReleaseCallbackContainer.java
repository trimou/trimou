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
package org.trimou.engine.resource;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.annotations.Internal;

/**
 * Abstract {@link ReleaseCallbackContainer}. It's not thread-safe.
 *
 * @author Martin Kouba
 */
@Internal
public abstract class AbstractReleaseCallbackContainer
        implements ReleaseCallbackContainer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractReleaseCallbackContainer.class);

    private List<ReleaseCallback> releaseCallbacks = null;

    /**
     * Release all the resources, i.e. invoke {@link ReleaseCallback#release()}
     * on each callback. All the callbacks are always invoked, even if one of
     * the invocation fails (throws unchecked exception). Callbacks are invoked
     * in the order in which they were registered.
     */
    public void release() {
        if (releaseCallbacks == null) {
            return;
        }
        for (ReleaseCallback callback : releaseCallbacks) {
            try {
                callback.release();
            } catch (Exception e) {
                LOGGER.warn(
                        "Exception occured during release callback invocation:",
                        e);
            }
        }
    }

    @Override
    public void registerReleaseCallback(ReleaseCallback callback) {
        if (releaseCallbacks == null) {
            releaseCallbacks = new LinkedList<>();
        }
        releaseCallbacks.add(callback);
    }

}
