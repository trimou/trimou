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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
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

    private RenderingContext renderingContext;

    private static final Logger logger = LoggerFactory
            .getLogger(TrimouExtension.class);

    /**
     *
     * @param event
     * @param beanManager
     */
    public void observeAfterBeanDiscovery(@Observes AfterBeanDiscovery event,
            BeanManager beanManager) {
        logger.info("Register context for @RenderingScoped");
        renderingContext = new RenderingContext();
        event.addContext(renderingContext);
        // Workaround to support CDI 1.0 and SE
        BeanManagerLocator.setExtensionProvidedBeanManager(beanManager);
    }

    public RenderingContext getRenderingContext() {
        return renderingContext;
    }

}
