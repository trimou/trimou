package org.trimou.engine.locale;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public class LocaleSupportFactory {

	/**
	 *
	 * @return the default locale support
	 */
	public LocaleSupport createLocateSupport() {
		return new DefaultLocaleSupport();
	}

}
