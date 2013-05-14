package org.trimou.servlet.i18n;

import java.util.Locale;

import org.trimou.engine.locale.LocaleSupport;
import org.trimou.servlet.RequestHolder;

/**
 *
 * @author Martin Kouba
 */
public class RequestLocaleSupport implements LocaleSupport {

	@Override
	public Locale getCurrentLocale() {
		return RequestHolder.getCurrentRequest().getLocale();
	}

}
