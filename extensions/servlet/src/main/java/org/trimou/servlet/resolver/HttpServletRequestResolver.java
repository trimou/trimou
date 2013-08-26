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
package org.trimou.servlet.resolver;

import static org.trimou.engine.priority.Priorities.rightAfter;

import javax.servlet.http.HttpServletRequest;

import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.servlet.RequestHolder;

/**
 *
 * @author Martin Kouba
 */
public class HttpServletRequestResolver extends AbstractResolver {

	public static final int SERVLET_REQUEST_RESOLVER_PRIORITY = rightAfter(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

	private static final String NAME_REQUEST = "request";

	public HttpServletRequestResolver() {
		this(SERVLET_REQUEST_RESOLVER_PRIORITY);
	}

	public HttpServletRequestResolver(int priority) {
		super(priority);
	}

	@Override
	public Object resolve(Object contextObject, String name,
			ResolutionContext context) {

		if (contextObject != null) {
			return null;
		}

		if (NAME_REQUEST.equals(name)) {
			HttpServletRequest request = RequestHolder.getCurrentRequest();
			if (request != null) {
				return new HttpServletRequestWrapper(request);
			}
		}
		return null;
	}

}
