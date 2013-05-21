package org.trimou.prettytime.resolver;

import static org.trimou.engine.priority.Priorities.after;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.resolver.ArrayIndexResolver;
import org.trimou.engine.resolver.i18n.LocaleAwareResolver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * PrettyTime resolver.
 *
 * @author Martin Kouba
 */
public class PrettyTimeResolver extends LocaleAwareResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(PrettyTimeResolver.class);

	public static final int PRETTY_TIME_RESOLVER_PRIORITY = after(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY);

	public static final ConfigurationKey MATCH_NAME_KEY = new SimpleConfigurationKey(
			PrettyTimeResolver.class.getName() + ".matchName", "prettyTime");

	private String matchName;

	/**
	 * Lazy loading cache of PrettyTime instances
	 */
	private LoadingCache<Locale, PrettyTime> prettyTimeCache;

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null || !matchName.equals(name)) {
			return null;
		}

		Date formattableObject = getFormattableObject(contextObject);

		if (formattableObject == null) {
			return null;
		}

		return prettyTimeCache.getUnchecked(getCurrentLocale()).format(
				formattableObject);
	}

	@Override
	public int getPriority() {
		return PRETTY_TIME_RESOLVER_PRIORITY;
	}

	@Override
	public List<ConfigurationKey> getConfigurationKeys() {
		return Collections.singletonList(MATCH_NAME_KEY);
	}

	@Override
	public void init(Configuration configuration) {
		super.init(configuration);

		matchName = configuration.getStringPropertyValue(MATCH_NAME_KEY);
		prettyTimeCache = CacheBuilder.newBuilder().maximumSize(10)
				.build(new CacheLoader<Locale, PrettyTime>() {

					@Override
					public PrettyTime load(Locale locale) throws Exception {
						return new PrettyTime(locale);
					}
				});
		logger.info("Initialized [matchName: {}]", matchName);
	}

	private Date getFormattableObject(Object contextObject) {
		if (contextObject instanceof Date) {
			return (Date) contextObject;
		} else if (contextObject instanceof Calendar) {
			return ((Calendar) contextObject).getTime();
		} else if (contextObject instanceof Long) {
			return new Date((Long) contextObject);
		}
		return null;
	}

}
