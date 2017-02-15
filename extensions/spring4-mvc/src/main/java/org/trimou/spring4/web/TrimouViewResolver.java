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

package org.trimou.spring4.web;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;

/**
 * Spring MVC {@link ViewResolver} for Trimou.
 */
public final class TrimouViewResolver extends AbstractTemplateViewResolver {

    static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";
    private SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator();
    private MustacheEngine engine;

    public TrimouViewResolver() {
        init();
        this.engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(loader)
                .build();
    }

    public TrimouViewResolver(final MustacheEngine engine) {
        init();
        this.engine = engine;
    }

    private void init() {
        setViewClass(requiredViewClass());
        setContentType(DEFAULT_CONTENT_TYPE);
        setPrefix(loader.getPrefix());
        setSuffix(loader.getSuffix());
    }

    /**
     * Returns true, if the cache of the {@link MustacheEngine} is enabled and the debug mode is disabled. Also Spring's
     * view resolution caching must be enabled.
     */
    @Override
    public boolean isCache() {
        return engine.getConfiguration().getBooleanPropertyValue(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED)
                && !engine.getConfiguration().getBooleanPropertyValue(EngineConfigurationKey.DEBUG_MODE)
                && super.isCache();
    }

    @Override
    protected Class<?> requiredViewClass() {
        return TrimouView.class;
    }

    /**
     * Set the mustache engine
     *
     * @param engine the mustache engine
     */
    public void setEngine(final MustacheEngine engine) {
        this.engine = engine;
    }

    @Override
    protected View loadView(final String viewName, final Locale locale) throws Exception {
        final Mustache mustache = engine.getMustache(viewName);
        if (mustache != null) {
            final TrimouView trimouView = (TrimouView) super.loadView(viewName, locale);
            trimouView.setMustache(mustache);
            return trimouView;
        }
        return null;
    }

    @Override
    public void setPrefix(final String prefix) {
        super.setPrefix(prefix);
        loader.setPrefix(prefix);
    }

    @Override
    public void setSuffix(final String suffix) {
        super.setSuffix(suffix);
        loader.setSuffix(suffix);
    }
}
