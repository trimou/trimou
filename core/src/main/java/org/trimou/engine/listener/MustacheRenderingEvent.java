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
package org.trimou.engine.listener;

import org.trimou.engine.resource.ReleaseCallbackContainer;

/**
 *
 * @author Martin Kouba
 * @see MustacheListener#renderingStarted(MustacheRenderingEvent)
 * @see MustacheListener#renderingFinished(MustacheRenderingEvent)
 */
public interface MustacheRenderingEvent extends ReleaseCallbackContainer {

    /**
     *
     * @return the template name
     * @see org.trimou.Mustache#getName()
     */
    public String getMustacheName();

    /**
     *
     * @return the generated id
     * @see org.trimou.Mustache#getGeneratedId()
     */
    public long getMustacheGeneratedId();

    /**
     * The value must be unique for every execution of a template within a
     * {@link org.trimou.engine.MustacheEngine} instance.
     *
     * @return the id of the rendering
     */
    public long getRenderingId();

}
