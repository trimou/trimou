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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.segment.ExtendSectionSegment;

/**
 *
 * @author Martin Kouba
 */
public class DebugExecutionContext extends DefaultExecutionContext {

	private static final Logger logger = LoggerFactory
			.getLogger(DebugExecutionContext.class);

	public DebugExecutionContext(List<Resolver> resolvers) {
		super(resolvers);
	}

	@Override
	public void push(Object baseObject) {
		super.push(baseObject);
		logger.debug("Push [type: {}, stack: {}]", baseObject.getClass(),
				contextObjectStack.size());
	}

	@Override
	public Object pop() {
		Object baseObject = super.pop();
		logger.debug("Pop [type: {}, stack: {}]", baseObject.getClass(),
				contextObjectStack.size());
		return baseObject;
	}

	@Override
	public void addDefiningSection(String name, ExtendSectionSegment segment) {
		super.addDefiningSection(name, segment);
		logger.debug("Extend section set [name: {}]", name);
	}

	/**
	 *
	 * @param contextObject
	 * @param key
	 * @return the resolved object
	 */
	protected Object resolve(Object contextObject, String key) {

		Object value = null;

		for (Resolver resolver : resolvers) {
			value = resolver.resolve(contextObject, key);
			if (value != null) {
				logger.debug("Value found [key: {}, resolver: {}]", key,
						resolver.getClass());
				break;
			}
		}
		return value;
	}
}
