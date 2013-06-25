package org.trimou.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.cdi.context.RenderingContext;

/**
 * Trimou CDI extension.
 *
 * @author Martin Kouba
 */
public class TrimouExtension implements Extension {

	private static final Logger logger = LoggerFactory
			.getLogger(TrimouExtension.class);

	/**
	 *
	 * @param event
	 */
	public void observeAfterBeanDiscovery(@Observes AfterBeanDiscovery event) {
		logger.info("Register context for @RenderingScoped");
		event.addContext(RenderingContext.INSTANCE);
	}

}