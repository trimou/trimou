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

import java.lang.reflect.Field;
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
 * Reflection-based resolver. First matching accesible methods defined on the
 * context object class and its superclasses are found, afterwards accesible
 * fields.
 *
 * @author Martin Kouba
 */
public class ReflectionResolver extends AbstractResolver implements
		RemovalListener<MemberKey, Optional<MemberWrapper>> {

	private static final Logger logger = LoggerFactory
			.getLogger(ReflectionResolver.class);

	public static final int REFLECTION_RESOLVER_PRIORITY = before(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

	/**
	 * Limit the size of the cache (e.g. to avoid problems when dynamic class
	 * compilation is involved).
	 */
	public static final ConfigurationKey MEMBER_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
			ReflectionResolver.class.getName() + ".memberCacheMaxSize", 5000l);

	/**
	 * Lazy loading cache of members
	 */
	private LoadingCache<MemberKey, Optional<MemberWrapper>> memberCache;

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null) {
			return null;
		}

		MemberWrapper wrapper = memberCache.getUnchecked(
				new MemberKey(contextObject, name)).orNull();

		if (wrapper == null) {
			return null;
		}

		try {
			return wrapper.getValue(contextObject);
		} catch (Exception e) {
			throw new MustacheException(
					MustacheProblem.RENDER_REFLECT_INVOCATION_ERROR, e);
		}
	}

	@Override
	public int getPriority() {
		return REFLECTION_RESOLVER_PRIORITY;
	}

	@Override
	public void init(Configuration configuration) {

		long memberCacheMaxSize = configuration
				.getLongPropertyValue(MEMBER_CACHE_MAX_SIZE_KEY);
		logger.info("Initialized [memberCacheMaxSize: {}]",
				memberCacheMaxSize);

		memberCache = CacheBuilder.newBuilder().maximumSize(memberCacheMaxSize)
				.removalListener(this)
				.build(new CacheLoader<MemberKey, Optional<MemberWrapper>>() {

					@Override
					public Optional<MemberWrapper> load(MemberKey key)
							throws Exception {

						// Find accesible method with the given name, no
						// parameters and non-void return type
						Method foundMethod = Reflections.getAccesibleMethod(
								key.getClazz(), key.getName());

						if (foundMethod != null) {
							return Optional
									.<MemberWrapper> of(new MethodWrapper(
											foundMethod));
						}

						// Find public field
						Field foundField = Reflections.getAccesibleField(key.getClazz(),
								key.getName());

						if (foundField != null) {
							return Optional
									.<MemberWrapper> of(new FieldWrapper(
											foundField));
						}

						// Member not found
						return Optional.absent();
					}
				});
	}

	@Override
	public List<ConfigurationKey> getConfigurationKeys() {
		return Collections
				.<ConfigurationKey> singletonList(MEMBER_CACHE_MAX_SIZE_KEY);
	}

	@Override
	public void onRemoval(
			RemovalNotification<MemberKey, Optional<MemberWrapper>> notification) {
		logger.debug("Remove [type: {}, key: {}, cause: {}]: ", notification
				.getKey().getClazz(), notification.getKey().getName(),
				notification.getCause());
	}

}
