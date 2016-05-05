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

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.cdi.context.RenderingContext;
import org.trimou.cdi.context.RenderingContextListener;
import org.trimou.cdi.resolver.CDIBeanResolver;
import org.trimou.engine.config.ConfigurationExtension;

/**
 *
 * @author Martin Kouba
 */
public class CDIConfigurationExtension implements ConfigurationExtension {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CDIConfigurationExtension.class);

    @Override
    public void register(ConfigurationExtensionBuilder builder) {
        BeanManager beanManager = BeanManagerLocator.locate();
        if (beanManager == null) {
            LOGGER.warn(
                    "CDI extension not operational - unable to locate BeanManager");
            return;
        }
        builder.addResolver(new CDIBeanResolver(beanManager));
        builder.addMustacheListener(
                new RenderingContextListener(getRenderingContext(beanManager)));
    }

    private RenderingContext getRenderingContext(BeanManager beanManager) {
        Set<Bean<?>> beans = beanManager.getBeans(TrimouExtension.class);
        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "Unable to get rendering context reference");
        }
        Bean<?> bean = beanManager.resolve(beans);
        CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        TrimouExtension trimouExtension = (TrimouExtension) beanManager
                .getReference(bean, TrimouExtension.class, ctx);
        return trimouExtension.getRenderingContext();
    }

}
