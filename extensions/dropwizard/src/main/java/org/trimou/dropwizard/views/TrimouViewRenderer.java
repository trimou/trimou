/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.dropwizard.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.exception.MustacheException;
import org.trimou.util.Strings;

import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;

/**
 * A Dropwizard {@link ViewRenderer} backed by Trimou.
 *
 * Localized template files support is enabled by default, but could be disabled to improve performance.
 *
 * @author Martin Kouba
 * @see Builder
 */
public class TrimouViewRenderer implements ViewRenderer {

    public static final String DEFAULT_SUFFIX = "trimou";

    private final MustacheEngine engine;

    private final String suffix;

    private final boolean hasLocalizedTemplates;

    /**
     *
     * @param engine
     * @param suffix
     * @param hasLocalizedTemplates
     */
    private TrimouViewRenderer(MustacheEngine engine, String suffix, boolean hasLocalizedTemplates) {
        this.engine = engine;
        this.suffix = "." + suffix;
        this.hasLocalizedTemplates = hasLocalizedTemplates;
    }

    @Override
    public boolean isRenderable(View view) {
        return view.getTemplateName().endsWith(suffix);
    }

    @Override
    public void render(View view, Locale locale, OutputStream output) throws IOException, WebApplicationException {

        Mustache template = null;

        if (hasLocalizedTemplates && locale != null) {
            // First try the Locale
            template = engine.getMustache(getLocalizedTemplateName(view.getTemplateName(), locale.toString()));
            if (template == null) {
                // Then only the language
                template = engine.getMustache(getLocalizedTemplateName(view.getTemplateName(), locale.getLanguage()));
            }
        }

        if (template == null) {
            template = engine.getMustache(view.getTemplateName());
        }

        if (template == null) {
            throw new FileNotFoundException("Template not found: " + view.getTemplateName());
        }

        final Writer writer = new OutputStreamWriter(output, engine.getConfiguration().getStringPropertyValue(EngineConfigurationKey.DEFAULT_FILE_ENCODING));

        try {
            template.render(writer, view);
        } catch (MustacheException e) {
            throw new IOException(e);
        } finally {
            writer.flush();
        }
    }

    private String getLocalizedTemplateName(String templateName, String localePart) {
        return Strings.removeSuffix(templateName, suffix) + "_" + localePart + suffix;
    }

    public static class Builder {

        private String suffix = DEFAULT_SUFFIX;

        private boolean hasLocalizedTemplates = true;

        public Builder setSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder setHasLocalizedTemplates(boolean hasLocalizedTemplates) {
            this.hasLocalizedTemplates = hasLocalizedTemplates;
            return this;
        }

        public TrimouViewRenderer build() {
            return build(MustacheEngineBuilder.newBuilder());
        }

        public TrimouViewRenderer build(MustacheEngineBuilder builder) {
            // Locator for views
            builder.addTemplateLocator(ClassPathTemplateLocator.builder(10).setClassLoader(this.getClass().getClassLoader()).build());
            // Locator for partials and template inheritance
            builder.addTemplateLocator(ClassPathTemplateLocator.builder(9).setClassLoader(this.getClass().getClassLoader()).setSuffix(suffix).build());
            return build(builder.build());
        }

        public TrimouViewRenderer build(MustacheEngine engine) {
            return new TrimouViewRenderer(engine, suffix, hasLocalizedTemplates);
        }

    }

}
