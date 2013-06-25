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

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.lambda.Lambda;

/**
 * Receives notifications about {@link Mustache} processing.
 *
 * Listeners are invoked in the order of their registration, except for
 * {@link #renderingFinished(DefaultMustacheRenderingEvent)} method which is
 * invoked in reverse order.
 *
 * Listeners are not invoked in case of any unchecked exception occurs under the
 * call stack during the processing.
 *
 * @author Martin Kouba
 * @see MustacheEngineBuilder#addMustacheListener(MustacheListener)
 */
public interface MustacheListener extends ConfigurationAware {

	/**
	 * Notification that a {@link Mustache} template was just compiled. Keep in
	 * mind that processing of a {@link Lambda} may also result in one-off
	 * template compilation - see also
	 * {@link Lambda#isReturnValueInterpolated()}.
	 *
	 * @param event
	 */
	void compilationFinished(MustacheCompilationEvent event);

	/**
	 * Rendering of a {@link Mustache} is about to start. Always use
	 * {@link MustacheRenderingEvent#registerReleaseCallback(ReleaseCallback)}
	 * to release all the necessary resources.
	 *
	 * @param event
	 */
	void renderingStarted(MustacheRenderingEvent event);

	/**
	 * Rendering of a {@link Mustache} is about to finish.
	 *
	 * Always use
	 * {@link MustacheRenderingEvent#registerReleaseCallback(ReleaseCallback)}
	 * to release all the necessary resources.
	 *
	 * @param event
	 */
	void renderingFinished(MustacheRenderingEvent event);

}
