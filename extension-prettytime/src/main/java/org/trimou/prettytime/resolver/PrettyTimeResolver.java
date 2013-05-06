package org.trimou.prettytime.resolver;

import static org.trimou.util.Priorities.after;

import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;
import org.trimou.api.engine.Configuration;
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

	public static final int PRETTY_TIME_RESOLVER_PRIORITY = after(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY);

	private static final String MATCH_NAME = "prettyTime";

	/**
	 * Lazy loading cache of PrettyTime instances
	 */
	private LoadingCache<Locale, PrettyTime> prettyTimeCache;

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null || !(contextObject instanceof Date)) {
			return null;
		}

		if (MATCH_NAME.equals(name)) {
			return prettyTimeCache.getUnchecked(getCurrentLocale()).format(
					(Date) contextObject);
		}
		return null;
	}

	@Override
	public int getPriority() {
		return PRETTY_TIME_RESOLVER_PRIORITY;
	}

	@Override
	public void init(Configuration configuration) {
		super.init(configuration);
		prettyTimeCache = CacheBuilder.newBuilder().maximumSize(10)
				.build(new CacheLoader<Locale, PrettyTime>() {

					@Override
					public PrettyTime load(Locale locale) throws Exception {
						return new PrettyTime(locale);
					}
				});
	}

}
