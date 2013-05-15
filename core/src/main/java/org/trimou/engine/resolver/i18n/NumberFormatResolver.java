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

import java.text.NumberFormat;

import org.trimou.engine.resolver.ArrayIndexResolver;

/**
 * Basic number formatting resolver.
 *
 * Acceptable names:
 * <ul>
 * <li>format - format number with the current locale</li>
 * <li>formatPercent - format number with the percentage pattern and the current
 * locale</li>
 * <li>formatCurrency - format number with the currency pattern and the current
 * locale</li>
 * </ul>
 *
 * @author Martin Kouba
 */
public class NumberFormatResolver extends LocaleAwareResolver {

	public static final int NUMBER_FORMAT_RESOLVER_PRIORITY = after(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY);

	private static final String NAME_FORMAT = "format";

	private static final String NAME_FORMAT_PERCENT = "formatPercent";

	private static final String NAME_FORMAT_CURR = "formatCurrency";

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject == null || !(contextObject instanceof Number)) {
			return null;
		}

		if (NAME_FORMAT.equals(name)) {
			return NumberFormat.getNumberInstance(
					localeSupport.getCurrentLocale()).format(contextObject);
		} else if (NAME_FORMAT_PERCENT.equals(name)) {
			return NumberFormat.getPercentInstance(
					localeSupport.getCurrentLocale()).format(contextObject);
		} else if (NAME_FORMAT_CURR.equals(name)) {
			return NumberFormat.getCurrencyInstance(
					localeSupport.getCurrentLocale()).format(contextObject);
		}
		return null;
	}

	@Override
	public int getPriority() {
		return NUMBER_FORMAT_RESOLVER_PRIORITY;
	}

}
