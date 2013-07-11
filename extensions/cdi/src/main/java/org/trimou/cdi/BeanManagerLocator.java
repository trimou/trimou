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

	private static BeanManager extensionProvidedBeanManager = null;

	/**
	 * Try to lookup the {@link BeanManager} reference.
	 *
	 * First try CDI 1.1 preferred way - javax.enterprise.inject.spi.CDI. Then
	 * CDI 1.0 compatible way - JNDI lookup. And finally extension provided
	 * manager (workaround to support CDI 1.0 and SE).
	 *
	 * @return {@link BeanManager} instance or <code>null</code>
	 */
	public static BeanManager locate() {

		BeanManager beanManager = locateCDI11();

		if (beanManager == null) {
			beanManager = locateJNDI();
		}

		if (beanManager != null) {
			return beanManager;
		} else if (extensionProvidedBeanManager != null) {
			logger.info("Finally using extension provided BeanManager instance");
			return extensionProvidedBeanManager;
		}
		return null;
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
				logger.warn("Unable to invoke CDI.current().getBeanManager()",
						e);
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
					logger.info("Unable to find BeanManager at: {}", name);
				}
				if (beanManager != null) {
					logger.info("BeanManager found at: {}", name);
					break;
				}
			}

		} catch (NamingException e) {
			logger.warn("JNDI lookup failed - unable to create initial context");
		}
		return beanManager;
	}

	static void setExtensionProvidedBeanManager(BeanManager beanManager) {
		extensionProvidedBeanManager = beanManager;
	}

}
