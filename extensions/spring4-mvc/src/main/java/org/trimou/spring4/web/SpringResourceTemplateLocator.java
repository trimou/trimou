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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.locator.AbstractTemplateLocator;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.priority.WithPriority;
import org.trimou.util.Strings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Trimou template locator implementation that uses a prefix, suffix and the Spring Resource abstraction to load
 * a template from a file, classpath, URL etc. A {@link TemplateLocator} is needed in the {@link MustacheEngine}
 * when you want to render partials.
 */
public final class SpringResourceTemplateLocator extends AbstractTemplateLocator implements ResourceLoaderAware {

    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".trimou";
    static final int DEFAULT_PRIORITY = WithPriority.DEFAULT_PRIORITY;
    static final String DEFAULT_CHARSET = "UTF-8";
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringResourceTemplateLocator.class);
    private String prefix = DEFAULT_PREFIX;
    private String suffix = DEFAULT_SUFFIX;
    private String charset = DEFAULT_CHARSET;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    public SpringResourceTemplateLocator() {
        super(DEFAULT_PRIORITY);
    }

    public SpringResourceTemplateLocator(final int priority, final String prefix, final String suffix) {
        super(priority);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Reader locate(final String name) {
        final String resourceLocation = prefix + name + suffix;
        try {
            if (Strings.isEmpty(charset)) {
                return new InputStreamReader(resourceLoader.getResource(resourceLocation).getInputStream());
            }
            return new InputStreamReader(resourceLoader.getResource(resourceLocation).getInputStream(), charset);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Charset not supported: {}", charset);
        } catch (IOException e) {
            LOGGER.warn("Template not found: {}", resourceLocation);
        }
        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the template prefix
     *
     * @param prefix the template prefix
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Set the template suffix
     *
     * @param suffix the template suffix
     */
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * Set the charset.
     *
     * @param charset the charset
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }
}
