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
package org.trimou.cdi.context;

import org.trimou.engine.listener.AbstractMustacheListener;
import org.trimou.engine.listener.MustacheRenderingEvent;
import org.trimou.engine.resource.ReleaseCallback;

/**
 * This listener initializes and destroys rendering context.
 *
 * @author Martin Kouba
 */
public final class RenderingContextListener extends AbstractMustacheListener {

	@Override
	public void renderingStarted(final MustacheRenderingEvent event) {
		RenderingContext.INSTANCE.initialize(event);
		event.registerReleaseCallback(new ReleaseCallback() {
			@Override
			public void release() {
				RenderingContext.INSTANCE.destroy(event);
			}
		});
	}

}
