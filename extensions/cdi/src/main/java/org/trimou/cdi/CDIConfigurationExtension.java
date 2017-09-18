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

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.trimou.cdi.context.RenderingContext;
import org.trimou.cdi.context.RenderingContextListener;
import org.trimou.cdi.resolver.CDIBeanResolver;
import org.trimou.engine.config.ConfigurationExtension;

/**
 *
 * @author Martin Kouba
 */
public class CDIConfigurationExtension implements ConfigurationExtension {

    @Override
    public void register(ConfigurationExtensionBuilder builder) {
        BeanManager beanManager = CDI.current().getBeanManager();
        builder.addResolver(new CDIBeanResolver(beanManager));
        builder.addMustacheListener(new RenderingContextListener(getRenderingContext(beanManager)));
    }

    private RenderingContext getRenderingContext(BeanManager beanManager) {
        try {
            return beanManager.getExtension(TrimouExtension.class).getRenderingContext();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to get rendering context reference", e);
        }
    }

}
