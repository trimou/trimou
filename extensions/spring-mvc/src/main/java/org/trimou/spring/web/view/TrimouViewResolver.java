/*
 * Copyright 2014 Minkyu Cho
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
package org.trimou.spring.web.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.handlebars.Helper;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

/**
 *
 * @author Minkyu Cho
 * @author Martin Kouba
 */
public class TrimouViewResolver extends AbstractTemplateViewResolver
        implements ViewResolver, InitializingBean {

    private String fileEncoding = System.getProperty("file.encoding");
    private boolean handlebarsSupport = true;
    private boolean debug = false;
    private boolean preCompile = false;
    private long cacheExpiration = 0l;
    private Map<String, Helper> helpers = new HashMap<>();
    private MustacheEngine engine;

    public TrimouViewResolver() {
        setViewClass(TrimouView.class);
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        TrimouView view = (TrimouView) super.buildView(viewName);
        try {
            view.setViewName(viewName);
            view.setEngine(engine);
            return view;
        } catch (Exception e) {
            throw new MustacheException(view.getUrl() + " : " + e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        engine = MustacheEngineBuilder.newBuilder()
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED,
                        isCache())
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT,
                        getCacheExpiration())
                .setProperty(EngineConfigurationKey.DEFAULT_FILE_ENCODING,
                        getFileEncoding())
                .setProperty(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED,
                        isHandlebarsSupport())
                .setProperty(EngineConfigurationKey.DEBUG_MODE, isDebug())
                .setProperty(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES,
                        isPreCompile())
                .registerHelpers(helpers)
                .addTemplateLocator(ServletContextTemplateLocator.builder()
                        .setPriority(1).setRootPath(getPrefix())
                        .setSuffix(getSuffixWithoutSeparator())
                        .setServletContext(getServletContext()).build())
                .build();
    }

    @Override
    protected Class<?> requiredViewClass() {
        return TrimouView.class;
    }

    public String getSuffixWithoutSeparator() {
        if (getSuffix().startsWith(".")) {
            return getSuffix().replace(".", "");
        }
        return getSuffix();
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public boolean isHandlebarsSupport() {
        return handlebarsSupport;
    }

    public void setHandlebarsSupport(boolean handlebarsSupport) {
        this.handlebarsSupport = handlebarsSupport;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isPreCompile() {
        return preCompile;
    }

    public void setPreCompile(boolean preCompile) {
        this.preCompile = preCompile;
    }

    public long getCacheExpiration() {
        return cacheExpiration;
    }

    public void setCacheExpiration(long cacheExpiration) {
        this.cacheExpiration = cacheExpiration;
    }

    public Map<String, Helper> getHelpers() {
        return helpers;
    }

    public void setHelpers(Map<String, Helper> helpers) {
        this.helpers = helpers;
    }

    public MustacheEngine getEngine() {
        return engine;
    }

    public void setEngine(MustacheEngine engine) {
        this.engine = engine;
    }
}