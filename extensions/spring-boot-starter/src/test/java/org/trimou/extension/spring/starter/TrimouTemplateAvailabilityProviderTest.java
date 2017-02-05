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

package org.trimou.extension.spring.starter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;

/**
 * Tests for {@link TrimouTemplateAvailabilityProvider}.
 */
@RunWith(JUnit4.class)
public class TrimouTemplateAvailabilityProviderTest {

    private final TemplateAvailabilityProvider provider = new TrimouTemplateAvailabilityProvider();

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    private final MockEnvironment environment = new MockEnvironment();

    @Test
    public void availabilityOfTemplateInDefaultLocation() throws Exception {
        assertThat(provider.isTemplateAvailable("home", environment, getClass().getClassLoader(), resourceLoader),
                is(true));
        assertThat(provider.isTemplateAvailable("prefix/prefixed", environment, getClass().getClassLoader(),
                resourceLoader), is(true));
    }

    @Test
    public void availabilityOfTemplateThatDoesNotExist() throws Exception {
        assertThat(provider.isTemplateAvailable("unknown", environment, getClass().getClassLoader(), resourceLoader),
                is(false));
    }

    @Test
    public void availabilityOfTemplateWithCustomPrefix() throws Exception {
        environment.setProperty("trimou.prefix", "classpath:/custom-templates/");
        assertThat(provider.isTemplateAvailable("custom", environment, getClass().getClassLoader(), resourceLoader),
                is(true));
    }

    @Test
    public void availabilityOfTemplateWithCustomSuffix() throws Exception {
        environment.setProperty("trimou.suffix", ".html");
        assertThat(provider.isTemplateAvailable("suffixed", environment, getClass().getClassLoader(), resourceLoader),
                is(true));
    }
}