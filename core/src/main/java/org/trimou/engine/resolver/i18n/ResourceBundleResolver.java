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
package org.trimou.engine.resolver.i18n;

import static org.trimou.engine.priority.Priorities.after;

import java.util.ResourceBundle;

import org.trimou.engine.resolver.ArrayIndexResolver;
import org.trimou.lambda.i18n.ResourceBundleLambda;

/**
 * {@link ResourceBundle} resolver. Unlike {@link ResourceBundleLambda} this
 * resolver is not limited to String-based values. However keep in mind that
 * resource bundle keys cannot contain dots.
 *
 * @author Martin Kouba
 * @see ResourceBundle
 * @see ResourceBundleLambda
 */
public class ResourceBundleResolver extends LocaleAwareResolver {

	private String baseName;

	private int priority;

	/**
	 *
	 * @param baseName
	 *            The base name of the resource bundle
	 */
	public ResourceBundleResolver(String baseName) {
		super();
		this.baseName = baseName;
		this.priority = after(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY);
	}

	/**
	 *
	 * @param baseName
	 *            The base name of the resource bundle
	 * @param priority
	 */
	public ResourceBundleResolver(String baseName, int priority) {
		super();
		this.baseName = baseName;
		this.priority = priority;
	}

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null && baseName.equals(name)) {
			return ResourceBundle.getBundle(baseName,
					localeSupport.getCurrentLocale());
		} else if (contextObject != null
				&& (contextObject instanceof ResourceBundle)) {
			ResourceBundle bundle = (ResourceBundle) contextObject;
			if (bundle.containsKey(name)) {
				return bundle.getObject(name);
			}
		}
		return null;
	}

	@Override
	public int getPriority() {
		return priority;
	}

}
