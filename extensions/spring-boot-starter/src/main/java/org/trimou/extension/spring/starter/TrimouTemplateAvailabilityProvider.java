/*
 * Copyright 2017 Trimou Team
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

package org.trimou.extension.spring.starter;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.trimou.spring4.web.SpringResourceTemplateLocator;

/**
 * {@link TemplateAvailabilityProvider} that provides availability information for Trimou view templates.
 */
public final class TrimouTemplateAvailabilityProvider implements TemplateAvailabilityProvider {

    public boolean isTemplateAvailable(final String view, final Environment environment, final ClassLoader classLoader,
            final ResourceLoader resourceLoader) {
        if (ClassUtils.isPresent("org.trimou.Mustache", classLoader)) {
            final PropertyResolver resolver =
                    new RelaxedPropertyResolver(environment, TrimouProperties.PROPERTY_PREFIX + '.');
            final String prefix = resolver.getProperty("prefix", SpringResourceTemplateLocator.DEFAULT_PREFIX);
            final String suffix = resolver.getProperty("suffix", SpringResourceTemplateLocator.DEFAULT_SUFFIX);
            final String resourceLocation = prefix + view + suffix;
            return resourceLoader.getResource(resourceLocation).exists();
        }
        return false;
    }
}
