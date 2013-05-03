package org.trimou.cdi.resolver;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Kouba
 */
public class BeanManagerLookup {

	private static final Logger logger = LoggerFactory
			.getLogger(BeanManagerLookup.class);

	private static final String CDI_CLASS_NAME = "javax.enterprise.inject.spi.CDI";

	/**
	 * Lookup {@link BeanManager} instance.
	 *
	 * @return {@link BeanManager} instance or <code>null</code>
	 */
	public static BeanManager lookup() {

		BeanManager beanManager = null;
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		Class<?> cdiClass = null;

		try {
			cdiClass = classLoader.loadClass(CDI_CLASS_NAME);
			logger.info("CDI 1.1 detected - using javax.enterprise.inject.spi.CDI to obtain BeanManager instance");
		} catch (ClassNotFoundException e) {
		} catch (NoClassDefFoundError e) {
		}

		if (cdiClass != null) {
			try {
				Object cdi = cdiClass.getMethod("current").invoke(null);
				Method getBeanManagerMethod = cdiClass
						.getMethod("getBeanManager");
				beanManager = (BeanManager) getBeanManagerMethod.invoke(cdi);
			} catch (Exception e) {
				// Extension fallback
			}
		}

		if (beanManager == null) {
			beanManager = CDIBeanResolverExtension.providedBeanManager;
			if (beanManager != null) {
				logger.info("CDI 1.0 detected - using extension to obtain BeanManager instance");
			}
		}
		return beanManager;
	}

}
