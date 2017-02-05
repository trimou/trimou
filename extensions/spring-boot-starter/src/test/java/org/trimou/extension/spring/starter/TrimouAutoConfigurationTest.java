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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "trimou.prefix:classpath:/trimou-templates/",
                "trimou.debug-mode:true"
        })
public class TrimouAutoConfigurationTest {

    @Autowired
    private EmbeddedWebApplicationContext context;

    private int port;

    @Before
    public void init() {
        this.port = context.getEmbeddedServletContainer().getPort();
    }

    private String getSimplePage() throws Exception {
        return new TestRestTemplate().getForObject("http://localhost:" + port, String.class);
    }

    private String minimize(final String content) {
        return content
                .replaceAll("\n", "")
                .replaceAll("\t", "")
                .replaceAll("  ", "");
    }

    @Test
    public void simplePage() throws Exception {
        final String body = getSimplePage();
        assertThat(body, containsString("Trimou rocks!"));
    }

    @Test
    public void partialPage() throws Exception {
        final String body = new TestRestTemplate().getForObject("http://localhost:" + port + "/partial", String.class);
        assertThat(body, containsString("Trimou rocks!"));
        assertThat(minimize(body), is(minimize(getSimplePage())));
    }

    @Test
    public void decoratedPage() throws Exception {
        final String body = new TestRestTemplate().getForObject("http://localhost:" + port + "/decorated", String.class);
        assertThat(body, containsString("Trimou rocks!"));
        assertThat(minimize(body), is(minimize(getSimplePage())));
    }
}