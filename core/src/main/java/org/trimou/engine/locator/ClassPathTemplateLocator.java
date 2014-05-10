/*
 * Copyright 2013 Martin Kouba
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
package org.trimou.engine.locator;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;

/**
 * Classpath template locator. There is a special {@link Builder} for
 * convenience.
 *
 * Note that for WAR archive, the classpath root is WEB-INF/classes (other parts
 * of the archive are not available).
 *
 * @author Martin Kouba
 */
public class ClassPathTemplateLocator extends FilePathTemplateLocator {

    private static final Logger logger = LoggerFactory
            .getLogger(ClassPathTemplateLocator.class);

    private final ClassLoader classLoader;

    /**
     *
     * @param priority
     * @param rootPath
     *            If null, no templates will be available for precompilation
     */
    public ClassPathTemplateLocator(int priority, String rootPath) {
        this(priority, rootPath, Thread.currentThread().getContextClassLoader());
    }

    /**
     *
     * @param priority
     * @param suffix
     *            If null, a full template name must be used
     * @param rootPath
     *            If null, no templates will be available for precompilation
     */
    public ClassPathTemplateLocator(int priority, String rootPath, String suffix) {
        this(priority, rootPath, suffix, Thread.currentThread()
                .getContextClassLoader());
    }

    /**
     *
     * @param priority
     * @param rootPath
     *            If null, no templates will be available for precompilation
     * @param classLoader
     *            Must not be null
     */
    public ClassPathTemplateLocator(int priority, String rootPath,
            ClassLoader classLoader) {
        this(priority, rootPath, null, classLoader);
    }

    /**
     *
     * @param priority
     * @param suffix
     *            If null, a full template name must be used
     * @param rootPath
     *            If null, no templates will be available for precompilation
     * @param classLoader
     *            Must not be null
     */
    public ClassPathTemplateLocator(int priority, String rootPath,
            String suffix, ClassLoader classLoader) {
        super(priority, rootPath, suffix);
        Checker.checkArgumentNotNull(classLoader);
        this.classLoader = classLoader;
        checkRootDir();
    }

    @Override
    public Set<String> getAllIdentifiers() {
        if (getRootPath() == null) {
            return Collections.emptySet();
        }
        return super.getAllIdentifiers();
    }

    @Override
    public Reader locateRealPath(String realPath) {
        String name = getRootPath() != null ? getRootPath()
                + addSuffix(realPath) : addSuffix(realPath);
        InputStream in = classLoader.getResourceAsStream(name);
        if (in == null) {
            return null;
        }
        logger.debug("Template located: {}", getRootPath() + realPath);
        try {
            return new InputStreamReader(in, getDefaultFileEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
    }

    @Override
    protected File getRootDir() {

        if (getRootPath() == null) {
            return null;
        }

        try {

            URL url = classLoader.getResource(getRootPath());

            if (url == null) {
                throw new MustacheException(
                        MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
                        "Root path resource not found: %s", getRootPath());
            }
            return new File(URLDecoder.decode(url.getFile(), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION, e);
        }
    }

    /**
     *
     * @param priority
     * @return a new instance of builder
     */
    public static Builder builder(int priority) {
        return new Builder(priority);
    }

    /**
     *
     * @author Martin Kouba
     */
    public static class Builder {

        private ClassLoader classLoader;

        private int priority;

        private String rootPath;

        private String suffix;

        private Builder(int priority) {
            this.priority = priority;
        }

        /**
         * If not set, TCCL is used.
         *
         * @param classLoader
         * @return builder
         */
        public Builder setClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        /**
         * If not set, no templates will be available for precompilation.
         *
         * @param rootPath
         * @return builder
         */
        public Builder setRootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        /**
         * If not set, a full template name must be used.
         *
         * @param suffix
         * @return builder
         */
        public Builder setSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public ClassPathTemplateLocator build() {
            return classLoader != null ? new ClassPathTemplateLocator(priority,
                    rootPath, suffix, classLoader)
                    : new ClassPathTemplateLocator(priority, rootPath, suffix);
        }

    }

}
