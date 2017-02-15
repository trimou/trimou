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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;

/**
 * Tests for {@link TrimouViewResolver}.
 */
@RunWith(JUnit4.class)
public class TrimouViewResolverTest {

    private TrimouViewResolver resolver;

    @Before
    public void init() {
        resolver = new TrimouViewResolver();
        resolver.setApplicationContext(new StaticWebApplicationContext());
        resolver.setServletContext(new MockServletContext());
        resolver.setPrefix("classpath:/trimou-templates/");
    }

    @Test
    public void resolveNonExistent() throws Exception {
        assertThat(resolver.resolveViewName("unknown", null), is(nullValue()));
    }

    @Test
    public void resolveNullLocale() throws Exception {
        assertThat(resolver.resolveViewName("home", null), is(notNullValue()));
    }

    @Test
    public void resolveDefaultLocale() throws Exception {
        assertThat(resolver.resolveViewName("home", Locale.US), is(notNullValue()));
    }

    @Test
    public void resolveDoubleLocale() throws Exception {
        assertThat(resolver.resolveViewName("home", Locale.CANADA_FRENCH), is(notNullValue()));
    }

    @Test
    public void resolveTripleLocale() throws Exception {
        assertThat(resolver.resolveViewName("home", new Locale("en", "GB", "cy")), is(notNullValue()));
    }

    @Test
    public void resolveSpecificLocale() throws Exception {
        assertThat(resolver.resolveViewName("home", new Locale("de")), is(notNullValue()));
    }

    @Test
    public void setsContentType() throws Exception {
        resolver.setCache(false);
        final View v1 = resolver.resolveViewName("home", null);
        assertThat(v1.getContentType(), is(TrimouViewResolver.DEFAULT_CONTENT_TYPE));

        resolver.setContentType("application/octet-stream");
        final View v2 = resolver.resolveViewName("home", null);
        assertThat(v2.getContentType(), is("application/octet-stream"));
    }

    @Test
    public void cacheEnabled() throws Exception {
        assertThat(resolver.isCache(), is(true));
    }

    @Test
    public void cacheDisabled() throws Exception {
        resolver.setEngine(MustacheEngineBuilder.newBuilder()
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED, false)
                .setProperty(EngineConfigurationKey.DEBUG_MODE, false)
                .build());
        assertThat(resolver.isCache(), is(false));
        resolver.setEngine(MustacheEngineBuilder.newBuilder()
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED, true)
                .setProperty(EngineConfigurationKey.DEBUG_MODE, true)
                .build());
        assertThat(resolver.isCache(), is(false));
        resolver.setEngine(MustacheEngineBuilder.newBuilder()
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED, false)
                .setProperty(EngineConfigurationKey.DEBUG_MODE, true)
                .build());
        assertThat(resolver.isCache(), is(false));
        resolver.setEngine(MustacheEngineBuilder.newBuilder()
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED, true)
                .setProperty(EngineConfigurationKey.DEBUG_MODE, false)
                .build());
        assertThat(resolver.isCache(), is(true));
        resolver.setCacheLimit(0);
        assertThat(resolver.isCache(), is(false));
    }

    @Test
    public void templateResourceInputStreamIsClosed() throws Exception {
        final Resource resource = mock(Resource.class);
        given(resource.exists()).willReturn(true);
        final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        final InputStream spyInputStream = spy(inputStream);
        given(resource.getInputStream()).willReturn(spyInputStream);
        final SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator();
        loader.setResourceLoader(new ResourceLoader() {
            public Resource getResource(final String location) {
                return resource;
            }

            public ClassLoader getClassLoader() {
                return getClass().getClassLoader();
            }
        });
        resolver.setEngine(MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(loader)
                .build());
        resolver.loadView("home", null);
        verify(spyInputStream).close();
    }
}