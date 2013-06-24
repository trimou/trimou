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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.resolver.ArrayIndexResolver;
import org.trimou.engine.resolver.ResolutionContext;

/**
 * Basic date and time formatting resolver.
 *
 * Acceptable names:
 * <ul>
 * <li>format - format date and time with predefined MEDIUM pattern</li>
 * <li>formatShort - format date and time with predefined SHORT pattern</li>
 * <li>formatDate - format date with predefined MEDIUM pattern</li>
 * <li>formatCustom - format date and time with custom pattern</li>
 * </ul>
 *
 * @author Martin Kouba
 */
public class DateTimeFormatResolver extends LocaleAwareResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(DateTimeFormatResolver.class);

	public static final int DATE_TIME_FORMAT_RESOLVER_PRIORITY = after(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY);

	public static final ConfigurationKey CUSTOM_PATTERN_KEY = new SimpleConfigurationKey(DateTimeFormatResolver.class.getName() + ".customPattern", "M/d/yy h:mm a");

	private static final String NAME_FORMAT = "format";

	private static final String NAME_FORMAT_SHORT = "formatShort";

	private static final String NAME_FORMAT_CUSTOM = "formatCustom";

	private static final String NAME_FORMAT_DATE = "formatDate";

	private String customPattern;

	@Override
	public Object resolve(Object contextObject, String name, ResolutionContext context) {

		if (contextObject == null) {
			return null;
		}

		Object formattableObject = getFormattableObject(contextObject);

		if (formattableObject == null) {
			return null;
		}

		if (NAME_FORMAT.equals(name)) {
			return format(DateFormat.MEDIUM, formattableObject);
		} else if (NAME_FORMAT_SHORT.equals(name)) {
			return format(DateFormat.SHORT, formattableObject);
		} else if (NAME_FORMAT_CUSTOM.equals(name)) {
			return formatCustom(formattableObject);
		}
		if (NAME_FORMAT_DATE.equals(name)) {
			return formatDate(DateFormat.MEDIUM, formattableObject);
		}
		return null;
	}

	@Override
	public int getPriority() {
		return DATE_TIME_FORMAT_RESOLVER_PRIORITY;
	}

	@Override
	public void init(Configuration configuration) {
		super.init(configuration);
		customPattern = configuration
				.getStringPropertyValue(CUSTOM_PATTERN_KEY);
		logger.info("Initialized [customPattern: {}]", customPattern);
	}

	@Override
	public Set<ConfigurationKey> getConfigurationKeys() {
		return Collections.singleton(CUSTOM_PATTERN_KEY);
	}

	private Object getFormattableObject(Object contextObject) {
		if (contextObject instanceof Date || contextObject instanceof Number) {
			return contextObject;
		} else if (contextObject instanceof Calendar) {
			return ((Calendar) contextObject).getTime();
		}
		return null;
	}

	private String format(int style, Object object) {
		return DateFormat.getDateTimeInstance(style, style,
				localeSupport.getCurrentLocale()).format(object);
	}

	private String formatDate(int style, Object object) {
		return DateFormat.getDateInstance(style,
				localeSupport.getCurrentLocale()).format(object);
	}

	private String formatCustom(Object object) {
		return new SimpleDateFormat(customPattern,
				localeSupport.getCurrentLocale()).format(object);
	}

}
