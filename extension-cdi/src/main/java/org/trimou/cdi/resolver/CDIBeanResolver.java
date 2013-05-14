package org.trimou.cdi.resolver;

import static org.trimou.engine.priority.Priorities.after;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.cdi.BeanManagerLocator;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.AbstractResolver;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Resolver for CDI beans with names.
 *
 * At the moment only built-in normal scopes are supported.
 *
 * @author Martin Kouba
 * @see Named
 */
@SuppressWarnings("rawtypes")
public class CDIBeanResolver extends AbstractResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(CDIBeanResolver.class);

	public static final int CDI_BEAN_RESOLVER_PRIORITY = after(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

	private BeanManager beanManager;

	private LoadingCache<String, Optional<Bean>> beanCache;

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject != null) {
			return null;
		}

		Optional<Bean> bean = beanCache.getUnchecked(name);

		if (!bean.isPresent()) {
			// Unsuccessful lookup already performed
			return null;
		}
		return getReference(bean.get());
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
				.getLongPropertyValue(CDIBeanResolverConfigurationKey.BEAN_CACHE_MAX_SIZE);
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
		logger.info("CDIBeanResolver initialized [beanCacheMaxSize: {}]",
				beanCacheMaxSize);

	}

	@Override
	public List<ConfigurationKey> getConfigurationKeys() {
		return Collections
				.<ConfigurationKey> singletonList(CDIBeanResolverConfigurationKey.BEAN_CACHE_MAX_SIZE);
	}

	private Object getReference(Bean<?> bean) {
		if (isSupportedScope(bean.getScope())) {
			return beanManager.getReference(bean, Object.class,
					beanManager.createCreationalContext(bean));
		}
		logger.warn("Bean with name {} has unsupported bean scope {}",
				bean.getName(), bean.getScope());
		return null;
	}

	private boolean isSupportedScope(Class<? extends Annotation> scope) {
		return RequestScoped.class.equals(scope)
				|| ApplicationScoped.class.equals(scope)
				|| ConversationScoped.class.equals(scope)
				|| SessionScoped.class.equals(scope);
	}

	public enum CDIBeanResolverConfigurationKey implements ConfigurationKey {

		BEAN_CACHE_MAX_SIZE(CDIBeanResolver.class.getName()
				+ ".beanCacheMaxSize", 500l), ;

		private String key;

		private Object defaultValue;

		CDIBeanResolverConfigurationKey(String key, Object defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}

		public String get() {
			return key;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}

	}

}
