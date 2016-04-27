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

import java.io.IOException;
import java.io.Writer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;

import org.trimou.engine.MustacheEngine;

/**
 *
 * @author Martin Kouba
 */
@Dependent
public class TrimouViewEngine implements ViewEngine {

    @Inject
    @ViewEngineConfig
    private MustacheEngine engine;

    @Inject
    @ViewEngineConfig
    private String suffix;

    @Override
    public boolean supports(String view) {
        return view.endsWith(suffix);
    }

    @Override
    public void processView(ViewEngineContext context)
            throws ViewEngineException {
        try {
            Models models = context.getModels();
            models.put("request", context.getRequest());
            models.put("locale", context.getRequest().getLocale());
            Writer writer = context.getResponse().getWriter();
            engine.getMustache(context.getView()).render(writer, models);
            writer.flush();
        } catch (IOException e) {
            throw new ViewEngineException(e);
        }
    }

}
