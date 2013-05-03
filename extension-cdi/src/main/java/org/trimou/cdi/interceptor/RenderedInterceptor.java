/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.cdi.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.trimou.api.Mustache;
import org.trimou.api.engine.MustacheEngine;

/**
 * WARNING: this code is considered experimental.
 *
 * @author Martin Kouba
 */
@Interceptor
@Rendered(template = "any")
public class RenderedInterceptor {

	@Inject
	private MustacheEngine engine;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@AroundInvoke
	public Object aroundInvoke(InvocationContext ctx) throws Exception {

		Object result = ctx.proceed();
		Map data = null;

		if (result instanceof Map) {
			data = (Map) result;
		}

		String templateName = getTemplateName(ctx.getMethod());

		Mustache mustache = engine.get(templateName);

		if (mustache == null) {
			throw new IllegalStateException("No template with name "
					+ templateName + " found");
		}
		return mustache.render(data);
	}

	private String getTemplateName(Method interceptedMethod) {

		String templateName = null;

		Rendered rendered = interceptedMethod.getAnnotation(Rendered.class);
		if (rendered != null) {
			templateName = rendered.template();
		}
		if (templateName == null) {
			rendered = interceptedMethod.getDeclaringClass().getAnnotation(
					Rendered.class);
		}
		if (templateName == null || templateName.isEmpty()) {
			throw new IllegalStateException("Invalid template name");
		}
		return templateName;
	}
}
