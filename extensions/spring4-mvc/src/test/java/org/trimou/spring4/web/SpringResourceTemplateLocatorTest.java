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

import java.io.IOException;
import java.io.Reader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link SpringResourceTemplateLocator}.
 */
@RunWith(JUnit4.class)
public class SpringResourceTemplateLocatorTest {

    @Test
    public void defaultConstructor() throws Exception {
        final SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator();
        assertThat(loader.getPriority(), is(SpringResourceTemplateLocator.DEFAULT_PRIORITY));
        assertThat(loader.getPrefix(), is(SpringResourceTemplateLocator.DEFAULT_PREFIX));
        assertThat(loader.getSuffix(), is(SpringResourceTemplateLocator.DEFAULT_SUFFIX));
        assertThat(loader.getCharset(), is(SpringResourceTemplateLocator.DEFAULT_CHARSET));
    }

    @Test
    public void availabilityOfTemplateInDefaultLocation() throws Exception {
        final SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator();
        final Reader r1 = loader.locate("home");
        assertThat(readContent(r1), is("home"));

        final Reader r2 = loader.locate("prefix/prefixed");
        assertThat(readContent(r2), is("prefixed"));
    }

    @Test
    public void availabilityOfTemplateThatDoesNotExist() throws Exception {
        final SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator();
        assertThat(loader.locate("unknown"), is(nullValue()));
    }

    @Test
    public void availabilityOfTemplateWithCustomPrefix() throws Exception {
        final SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator(1,
                "classpath:/custom-templates/", SpringResourceTemplateLocator.DEFAULT_SUFFIX);
        final Reader reader = loader.locate("custom");
        assertThat(readContent(reader), is("custom"));
    }

    @Test
    public void availabilityOfTemplateWithCustomSuffix() throws Exception {
        final SpringResourceTemplateLocator loader =
                new SpringResourceTemplateLocator(1, SpringResourceTemplateLocator.DEFAULT_PREFIX, ".html");
        final Reader reader = loader.locate("suffixed");
        assertThat(readContent(reader), is("suffixed"));
    }

    @Test
    public void unsupportedCharset() throws Exception {
        final SpringResourceTemplateLocator loader = new SpringResourceTemplateLocator();
        loader.setCharset("8-FTU");
        assertThat(loader.locate("home"), is(nullValue()));
    }

    private String readContent(final Reader reader) throws IOException {
        assertThat(reader, notNullValue());
        final StringBuilder sb = new StringBuilder();
        try {
            final char[] buffer = new char[8 * 1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer, 0, buffer.length)) != -1) {
                sb.append(buffer, 0, bytesRead);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }
}