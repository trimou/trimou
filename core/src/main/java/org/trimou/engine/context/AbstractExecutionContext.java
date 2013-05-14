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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.util.Checker;
import org.trimou.util.Strings;

/**
 * Abstract execution context.
 *
 * @author Martin Kouba
 */
public abstract class AbstractExecutionContext implements ExecutionContext {

	/**
	 * Immutable ordered list of resolvers
	 */
	protected List<Resolver> resolvers;

	/**
	 * LIFO stack of context objects
	 */
	protected Deque<Object> contextObjectStack = new ArrayDeque<Object>(8);

	/**
	 * Map of defining/overriding sections
	 */
	protected Map<String, ExtendSectionSegment> definingSections = null;

	/**
	 *
	 * @param resolvers
	 */
	protected AbstractExecutionContext(List<Resolver> resolvers) {
		this.resolvers = resolvers;
	}

	@Override
	public void push(Object contextObject) {
		Checker.checkArgumentNull(contextObject);
		contextObjectStack.addFirst(contextObject);
	}

	@Override
	public Object pop() {
		return contextObjectStack.removeFirst();
	}

	@Override
	public void addDefiningSection(String name, ExtendSectionSegment segment) {
		if (definingSections == null) {
			// Lazy init - ok, context is not thread-safe
			definingSections = new HashMap<String, ExtendSectionSegment>(8);
		}
		if (!definingSections.containsKey(name)) {
			definingSections.put(name, segment);
		}
	}

	@Override
	public ExtendSectionSegment getDefiningSection(String name) {
		if (definingSections == null || definingSections.isEmpty()) {
			return null;
		}
		return definingSections.get(name);
	}

	/**
	 * Resolve the leading context object (the first part of the key). E.g.
	 * <code>foo</code> in <code>{{foo.bar.name}}</code> may identify a property
	 * of some context object on the stack (passed data, section iteration, nested
	 * context, ...), or some context and data unrelated object (e.g. CDI bean).
	 *
	 * @param name
	 * @return the resolved leading base object
	 */
	protected Object resolveLeadingContextObject(String name) {

		Object leading = null;

		for (Object contextObject : contextObjectStack) {
			leading = resolve(contextObject, name);
			if (leading != null) {
				// Skip following
				break;
			}
		}
		if (leading == null) {
			// Try to resolve context unrelated objects
			leading = resolve(null, name);
		}
		return leading;
	}

	/**
	 *
	 * @param contextObject
	 * @param name
	 * @return the resolved object
	 */
	protected Object resolve(Object contextObject, String name) {

		Object value = null;

		for (Resolver resolver : resolvers) {
			value = resolver.resolve(contextObject, name);
			if (value != null) {
				break;
			}
		}
		return value;
	}

	protected boolean isCompoundKey(String key) {
		return !key.equals(Strings.KEY_SEPARATOR)
				&& key.contains(Strings.KEY_SEPARATOR);
	}

	protected String[] splitKey(String key) {
		return StringUtils.split(key, Strings.KEY_SEPARATOR);
	}

}
