package org.trimou.servlet.i18n;

import java.util.Locale;

import org.trimou.servlet.RequestHolder;
import org.trimou.spi.engine.LocaleSupport;

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
