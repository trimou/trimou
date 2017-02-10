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

package org.trimou.spring4.i18n;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locale.FixedLocaleSupport;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Arrays;

import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class SpringMessageSourceHelperTest {

    private final MessageSource messageSource = initMessageSource();

    private MustacheEngine engine;

    private static MessageSource initMessageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setCacheSeconds(5);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setBasename("classpath:messages");
        return messageSource;
    }

    @Before
    public void setup() {
        engine = MustacheEngineBuilder.newBuilder()
                .setLocaleSupport(FixedLocaleSupport.from(Locale.ENGLISH))
                .registerHelper("msg", new SpringMessageSourceHelper(messageSource))
                .build();
    }

    @Test
    public void compileException() throws Exception {
        final String template = "{{msg}}";
        try {
            engine.compileMustache("i18n", template);
            fail();
        } catch (MustacheException e) {
            assertThat(e.getCode(), is(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE));
        }
    }

    @Test
    public void interpolation() throws Exception {
        final String t1 = "{{msg 'label.hello'}}";
        final Mustache m1 = engine.compileMustache("i18n-en", t1);
        assertThat(m1.render(Arrays.EMPTY_OBJECT_ARRAY), is("Hello"));
        final String t2 = "{{msg 'label.hello' locale='de'}}";
        final Mustache m2 = engine.compileMustache("i18n-de", t2);
        assertThat(m2.render(Arrays.EMPTY_OBJECT_ARRAY), is("Hallo"));
    }

    @Test
    public void defaultLocaleSupport() throws Exception {
        engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("msg", new SpringMessageSourceHelper(messageSource))
                .build();
        final String template = "{{msg 'label.hello'}}";
        final Mustache mustache = engine.compileMustache("i18n", template);
        assertThat(mustache.render(Arrays.EMPTY_OBJECT_ARRAY), is("Hello"));
    }

    @Test
    public void missingMessageCode() throws Exception {
        final String template = "{{msg 'label.unknown'}}";
        final Mustache mustache = engine.compileMustache("i18n", template);
        assertThat(mustache.render(Arrays.EMPTY_OBJECT_ARRAY), is("label.unknown"));
    }

    @Test
    public void withArguments() throws Exception {
        final String t1 = "{{msg 'label.greeting' 'Michael'}}";
        final Mustache m1 = engine.compileMustache("i18n", t1);
        assertThat(m1.render(Arrays.EMPTY_OBJECT_ARRAY), is("Hello Michael!"));

        final String t2 = "{{msg 'label.greeting' 'Michael' locale='de'}}";
        final Mustache m2 = engine.compileMustache("i18n", t2);
        assertThat(m2.render(Arrays.EMPTY_OBJECT_ARRAY), is("Hallo Michael!"));
    }

    @Test
    public void defaultMessage() throws Exception {
        final String template = "{{msg 'label.missing' defaultMessage='Missing label'}}";
        final Mustache mustache = engine.compileMustache("i18n", template);
        assertThat(mustache.render(Arrays.EMPTY_OBJECT_ARRAY), is("Missing label"));
    }
}