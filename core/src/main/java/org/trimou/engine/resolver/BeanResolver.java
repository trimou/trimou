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
package org.trimou.engine.resolver;

import static org.trimou.engine.priority.Priorities.before;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.priority.WithPriority;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Reflections;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Reflection-based resolver. Resolves read methods defined on the context
 * object class and its superclasses.
 *
 * This resolver doesn't support reading fields.
 *
 * @see Reflections#getReadMethod(Class, String)
 * @see Reflections#isReadMethod(Method)
 */
public class BeanResolver extends AbstractResolver implements
		RemovalListener<ReadMethodKey, Optional<Method>> {

	private static final Logger logger = LoggerFactory
			.getLogger(BeanResolver.class);

	public static final int BEAN_RESOLVER_PRIORITY = before(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

	/**
	 * Limit the size of the cache (e.g. to avoid problems when dynamic class
	 * compilation is involved).
	 */
	public static final ConfigurationKey READ_METHODS_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
			BeanResolver.class.getName() + ".readMethodsCacheMaxSize", 5000l);

	/**
	 * Lazy loading cache of read methods for already requested types
	 */
	private LoadingCache<ReadMethodKey, Optional<Method>> readMethodsCache;

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null) {
			return null;
		}

		Method readMethod = readMethodsCache.getUnchecked(
				new ReadMethodKey(contextObject.getClass(), name)).orNull();

		if (readMethod == null) {
			return null;
		}

		try {
			return readMethod.invoke(contextObject);
		} catch (Exception e) {
			throw new MustacheException(
					MustacheProblem.RENDER_REFLECT_INVOCATION_ERROR, e);
		}
	}

	@Override
	public int getPriority() {
		return BEAN_RESOLVER_PRIORITY;
	}

	@Override
	public void init(Configuration configuration) {

		long readMethodsCacheMaxSize = configuration
				.getLongPropertyValue(READ_METHODS_CACHE_MAX_SIZE_KEY);
		logger.info("Initialized [readMethodsCacheMaxSize: {}]",
				readMethodsCacheMaxSize);

		readMethodsCache = CacheBuilder.newBuilder()
				.maximumSize(readMethodsCacheMaxSize).removalListener(this)
				.build(new CacheLoader<ReadMethodKey, Optional<Method>>() {

					@Override
					public Optional<Method> load(ReadMethodKey key)
							throws Exception {

						Method foundMethod = Reflections.getReadMethod(
								key.getClazz(), key.getName());
						return Optional.fromNullable(foundMethod);
					}
				});
	}

	@Override
	public List<ConfigurationKey> getConfigurationKeys() {
		return Collections
				.<ConfigurationKey> singletonList(READ_METHODS_CACHE_MAX_SIZE_KEY);
	}

	@Override
	public void onRemoval(
			RemovalNotification<ReadMethodKey, Optional<Method>> notification) {
		logger.debug("Remove [type: {}, key: {}, cause: {}]: ", notification
				.getKey().getClazz(), notification.getKey().getName(),
				notification.getCause());
	}

}
