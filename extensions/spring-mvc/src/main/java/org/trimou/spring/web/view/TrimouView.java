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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.trimou.engine.MustacheEngine;
import org.trimou.exception.MustacheException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;

/**
 * This is the spring view use to generate the content based on
 * a Mustache template.
 *
 * @author Minkyu Cho
 */
public class TrimouView extends AbstractTemplateView {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrimouView.class);

    private String viewName;

    private MustacheEngine engine;

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());
        exposeRequestAttributesAsModel(model, request);
        final Writer writer = response.getWriter();
        try {
            engine.getMustache(viewName).render(writer, model);
        } catch (NullPointerException e) {
            throw new MustacheException(getUrl() + " is not exist.", e);
        } finally {
            writer.flush();
        }
    }

    private void exposeRequestAttributesAsModel(Map<String, Object> model, HttpServletRequest request) {
        if (request == null || request.getAttributeNames() == null) {
            return;
        }

        Enumeration attributeNames = request.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String attributeName = (String) attributeNames.nextElement();
            model.put(attributeName, request.getAttribute(attributeName));
        }
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public void setEngine(MustacheEngine engine) {
        this.engine = engine;
    }
}
