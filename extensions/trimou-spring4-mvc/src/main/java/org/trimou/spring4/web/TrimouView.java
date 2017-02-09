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

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.trimou.Mustache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Spring MVC {@link View} using the Trimou template engine.
 */
public final class TrimouView extends AbstractTemplateView {

    private Mustache mustache;

    /**
     * Create a new {@link TrimouView} instance.
     */
    public TrimouView() {
    }

    /**
     * Create a new {@link TrimouView} instance with the specified template.
     *
     * @param mustache the mustache template
     */
    public TrimouView(final Mustache mustache) {
        this.mustache = mustache;
    }

    /**
     * Set the Mustache template that should actually be rendered.
     *
     * @param mustache the mustache template
     */
    public void setMustache(final Mustache mustache) {
        this.mustache = mustache;
    }

    @Override
    protected void renderMergedTemplateModel(final Map<String, Object> model,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (mustache != null) {
            mustache.render(response.getWriter(), model);
        }
    }
}
