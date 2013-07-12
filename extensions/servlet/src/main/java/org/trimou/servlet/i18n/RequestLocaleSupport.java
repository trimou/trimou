package org.trimou.servlet.i18n;

import java.util.Locale;

import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.servlet.RequestHolder;

/**
 *
 * @author Martin Kouba
 */
public class RequestLocaleSupport extends AbstractConfigurationAware implements
        LocaleSupport {

    @Override
    public Locale getCurrentLocale() {
        return RequestHolder.getCurrentRequest().getLocale();
    }

}
