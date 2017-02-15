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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

@RunWith(JUnit4.class)
public class TrimouViewTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

    @Before
    public void init() {
        context.refresh();
        final MockServletContext servletContext = new MockServletContext();
        context.setServletContext(servletContext);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }

    @Test
    public void viewResolves() throws Exception {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        final TrimouView view = new TrimouView(engine.compileMustache("hello", "Hello {{msg}}"));
        view.setApplicationContext(context);
        view.render(Collections.singletonMap("msg", "world!"), request, response);
        assertThat(response.getContentAsString(), is("Hello world!"));
    }
}