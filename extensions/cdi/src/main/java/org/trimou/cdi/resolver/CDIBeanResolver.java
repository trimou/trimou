package org.trimou.cdi.resolver;

import static org.trimou.engine.priority.Priorities.after;

import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.cdi.BeanManagerLocator;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resource.ReleaseCallback;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * CDI beans resolver. Note that only beans with a name (i.e. annotated with
 * {@link Named}) are resolvable.
 *
 * Similarly to the CDI and Unified EL integration, instance of a dependent bean
 * exists to service just a single tag evaluation.
 *
 * @author Martin Kouba
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CDIBeanResolver extends AbstractResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(CDIBeanResolver.class);

	public static final int CDI_BEAN_RESOLVER_PRIORITY = after(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

	public static final ConfigurationKey BEAN_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
			CDIBeanResolver.class.getName() + ".beanCacheMaxSize", 1000l);

	private BeanManager beanManager;

	private LoadingCache<String, Optional<Bean>> beanCache;

	@Override
	public Object resolve(Object contextObject, String name,
			ResolutionContext context) {

		if (contextObject != null) {
			return null;
		}

		Optional<Bean> bean = beanCache.getUnchecked(name);

		if (!bean.isPresent()) {
			// Unsuccessful lookup already performed
			return null;
		}
		return getReference(bean.get(), context);
	}

	@Override
	public int getPriority() {
		return CDI_BEAN_RESOLVER_PRIORITY;
	}

	@Override
	public void init(Configuration configuration) {

		// Init BeanManager
		beanManager = BeanManagerLocator.locate();
		if (beanManager == null) {
			throw new IllegalStateException(
					"BeanManager not set - invalid resolver configuration");
		}
		// Init cache max size
		long beanCacheMaxSize = configuration
				.getLongPropertyValue(BEAN_CACHE_MAX_SIZE_KEY);
		beanCache = CacheBuilder.newBuilder().maximumSize(beanCacheMaxSize)
				.build(new CacheLoader<String, Optional<Bean>>() {

					@Override
					public Optional<Bean> load(String name) throws Exception {

						Set<Bean<?>> beans = beanManager.getBeans(name);

						// Check required for CDI 1.0
						if (beans == null || beans.isEmpty()) {
							return Optional.absent();
						}

						try {
							return Optional.of((Bean) beanManager
									.resolve(beans));
						} catch (AmbiguousResolutionException e) {
							logger.warn(
									"An ambiguous EL name exists [name: {}]",
									name);
							return Optional.absent();
						}
					}
				});
		logger.info("Initialized [beanCacheMaxSize: {}]", beanCacheMaxSize);
	}

	@Override
	public Set<ConfigurationKey> getConfigurationKeys() {
		return Collections
				.<ConfigurationKey> singleton(BEAN_CACHE_MAX_SIZE_KEY);
	}

	private Object getReference(Bean bean, ResolutionContext context) {

		CreationalContext creationalContext = beanManager
				.createCreationalContext(bean);

		if (Dependent.class.equals(bean.getScope())) {
			Object reference = bean.create(creationalContext);
			context.registerReleaseCallback(new DependentDestroyCallback(bean,
					creationalContext, reference));
			return reference;

		} else {
			return beanManager.getReference(bean, Object.class,
					creationalContext);
		}
	}

	static class DependentDestroyCallback implements ReleaseCallback {

		private final Bean bean;

		private final CreationalContext creationalContext;

		private final Object instance;

		private DependentDestroyCallback(Bean<?> bean,
				CreationalContext<?> creationalContext, Object instance) {
			super();
			this.bean = bean;
			this.creationalContext = creationalContext;
			this.instance = instance;
		}

		@Override
		public void release() {
			bean.destroy(instance, creationalContext);
		}

	}

}
