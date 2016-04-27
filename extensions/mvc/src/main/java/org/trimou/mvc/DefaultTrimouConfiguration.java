/*
 * Copyright 2016 Martin Kouba
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
package org.trimou.mvc;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.mvc.MvcContext;
import javax.mvc.engine.ViewEngine;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Configuration;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.handlebars.HelpersBuilder;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

/**
 * Default producer for {@link MustacheEngine} and suffix used by
 * {@link TrimouViewEngine}.
 *
 * @author Martin Kouba
 */
@Dependent
public class DefaultTrimouConfiguration {

    @Inject
    private MvcContext mvc;

    @Inject
    private ServletContext servletContext;

    @Produces
    @ViewEngineConfig
    public MustacheEngine getMustacheEngine() {
        return MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.extra().build())
                .addTemplateLocator(ServletContextTemplateLocator.builder()
                        .setRootPath(getProperty(mvc.getConfig(),
                                ViewEngine.VIEW_FOLDER,
                                ViewEngine.DEFAULT_VIEW_FOLDER).toString())
                        .setServletContext(servletContext).build())
                .build();
    }

    @Produces
    @ViewEngineConfig
    public String getSuffix() {
        return ".trimou";
    }

    public static Object getProperty(Configuration config, String name,
            Object defaultValue) {
        final Object value = config.getProperty(name);
        return value != null ? value : defaultValue;
    }
}
