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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.trimou.engine.MustacheEngineBuilder;

/**
 * Tests for {@link TrimouConfigurer}.
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(classes = {TestApplication.class, TrimouConfigurerTest.TrimouConfigurationDecorator.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrimouConfigurerTest {

    @Autowired
    private EmbeddedWebApplicationContext context;

    private int port;

    @Before
    public void init() {
        this.port = context.getEmbeddedServletContainer().getPort();
    }

    @Test
    public void registerGlobalData() throws Exception {
        final String body = new TestRestTemplate().getForObject("http://localhost:" + port, String.class);
        assertThat(body, containsString("(c) Trimou Team"));
    }

    @Configuration
    static class TrimouConfigurationDecorator implements TrimouConfigurer {

        @Override
        public void configure(final MustacheEngineBuilder engineBuilder) {
            engineBuilder.addGlobalData("footer", "(c) Trimou Team");
        }
    }
}
