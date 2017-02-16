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

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.priority.WithPriority;
import org.trimou.spring4.web.SpringResourceTemplateLocator;
import org.trimou.spring4.web.TrimouViewResolver;

/**
 * {@link EnableAutoConfiguration} Spring Boot Auto-Configuration for Trimou.
 */
@Configuration
@ConditionalOnClass(Mustache.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(TrimouProperties.class)
public class TrimouAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrimouAutoConfiguration.class);

    private final TrimouProperties properties;

    private final ApplicationContext applicationContext;

    public TrimouAutoConfiguration(final TrimouProperties properties, final ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    protected void checkTemplateLocationExists() {
        if (properties.isCheckTemplateLocation()) {
            final TemplateLocation location = new TemplateLocation(properties.getPrefix());
            if (!location.exists(applicationContext)) {
                LOGGER.warn("Cannot find template location: " + location
                        + " (please add some templates, check your Trimou "
                        + "configuration, or set trimou.check-template-location=false)");
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(MustacheEngine.class)
    public MustacheEngine mustacheEngine(final SpringResourceTemplateLocator springResourceTemplateLocator) {
        final MustacheEngineBuilder mustacheEngineBuilder = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(springResourceTemplateLocator);
        properties.applyToTrimouMustacheEngineBuilder(mustacheEngineBuilder);
        for (TrimouConfigurer configurer : applicationContext.getBeansOfType(TrimouConfigurer.class).values()) {
            configurer.configure(mustacheEngineBuilder);
        }
        return mustacheEngineBuilder.build();
    }

    @Bean
    @ConditionalOnMissingBean(TemplateLocator.class)
    protected SpringResourceTemplateLocator springResourceTemplateLocator() {
        final SpringResourceTemplateLocator locator =
                new SpringResourceTemplateLocator(WithPriority.DEFAULT_PRIORITY, properties.getPrefix(),
                        properties.getSuffix());
        locator.setCharset(properties.getCharsetName());
        return locator;
    }

    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnClass(Servlet.class)
    protected static class TrimouWebConfiguration {

        private final TrimouProperties properties;

        protected TrimouWebConfiguration(final TrimouProperties properties) {
            this.properties = properties;
        }

        @Bean
        @ConditionalOnMissingBean(TrimouViewResolver.class)
        @ConditionalOnProperty(name = "trimou.enabled", matchIfMissing = true)
        public TrimouViewResolver trimouViewResolver(final MustacheEngine engine) {
            final TrimouViewResolver resolver = new TrimouViewResolver(engine);
            properties.applyToViewResolver(resolver);
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
            return resolver;
        }
    }
}
