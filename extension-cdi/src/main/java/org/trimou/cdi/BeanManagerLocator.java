package org.trimou.cdi;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Kouba
 */
public class BeanManagerLocator {

	private static final Logger logger = LoggerFactory
			.getLogger(BeanManagerLocator.class);

	private static final String CDI_CLASS_NAME = "javax.enterprise.inject.spi.CDI";

	private static final String[] JNDI_NAMES = { "java:comp/BeanManager",
			"java:comp/env/BeanManager" };

	/**
	 * Lookup {@link BeanManager} instance.
	 *
	 * @return {@link BeanManager} instance or <code>null</code>
	 */
	public static BeanManager locate() {

		BeanManager beanManager = locateCDI11();

		if (beanManager == null) {
			beanManager = locateJNDI();
		}
		return beanManager;
	}

	private static BeanManager locateCDI11() {

		BeanManager beanManager = null;
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		Class<?> cdiClass = null;

		try {
			cdiClass = classLoader.loadClass(CDI_CLASS_NAME);
			logger.info("CDI 1.1 - using javax.enterprise.inject.spi.CDI to obtain BeanManager instance");
		} catch (ClassNotFoundException e) {
			// CDI 1.0
		} catch (NoClassDefFoundError e) {
			// CDI 1.0
		}

		if (cdiClass != null) {
			try {
				Object cdi = cdiClass.getMethod("current").invoke(null);
				Method getBeanManagerMethod = cdiClass
						.getMethod("getBeanManager");
				beanManager = (BeanManager) getBeanManagerMethod.invoke(cdi);
			} catch (Exception e) {
				// Reflection invocation failed
			}
		}
		return beanManager;
	}

	private static BeanManager locateJNDI() {

		BeanManager beanManager = null;

		logger.info("CDI 1.0 - using JNDI to obtain BeanManager instance");

		try {

			Context ctx = new InitialContext();

			for (String name : JNDI_NAMES) {
				try {
					beanManager = (BeanManager) ctx.lookup(name);
				} catch (NamingException e) {
					// Not found
				}
				if (beanManager != null) {
					logger.info("BeanManager found: " + name);
					break;
				}
			}

		} catch (NamingException e) {
			logger.warn("JNDI lookup failed - unable to create initial context");
		}
		return beanManager;
	}

}
