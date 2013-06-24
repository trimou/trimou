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

import java.util.ArrayList;
import java.util.List;

import org.trimou.annotations.Internal;
import org.trimou.engine.resolver.ResolutionContext;

/**
 * A wrapper class for the resolved object and release callbacks.
 *
 * @author Martin Kouba
 */
@Internal
public final class ValueWrapper implements ResolutionContext {

	private Object value = null;

	private List<ResolutionContext.ReleaseCallback> releaseCallbacks = null;

	ValueWrapper() {
		super();
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

	/**
	 * Release all the resolution-specific resources. This method must be always
	 * called after the wrapper is used, even if the resolved object is
	 * <code>null</code> (there might be still some callbacks registered).
	 */
	public void release() {
		if (releaseCallbacks != null) {
			for (ResolutionContext.ReleaseCallback callback : releaseCallbacks) {
				callback.release();
			}
		}
	}

	@Override
	public void registerReleaseCallback(ResolutionContext.ReleaseCallback callback) {
		if (releaseCallbacks == null) {
			releaseCallbacks = new ArrayList<ResolutionContext.ReleaseCallback>(5);
		}
		releaseCallbacks.add(callback);
	}

}
