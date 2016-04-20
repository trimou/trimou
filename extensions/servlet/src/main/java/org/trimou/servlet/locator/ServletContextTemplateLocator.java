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
package org.trimou.servlet.locator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.locator.PathTemplateLocator;
import org.trimou.engine.priority.WithPriority;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.servlet.RequestHolder;
import org.trimou.util.Strings;

/**
 * Servlet context template locator.
 *
 * @author Martin Kouba
 */
public class ServletContextTemplateLocator extends PathTemplateLocator<String> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ServletContextTemplateLocator.class);

    private final ServletContext servletContext;

    /**
     *
     * @param priority
     * @param rootPath
     * @see Builder
     */
    public ServletContextTemplateLocator(int priority, String rootPath) {
        this(priority, rootPath, null, null);
    }

    /**
     *
     * @param priority
     * @param rootPath
     * @param servletContext
     * @see Builder
     */
    public ServletContextTemplateLocator(int priority, String rootPath,
            ServletContext servletContext) {
        this(priority, rootPath, null, servletContext);
    }

    /**
     *
     * @param priority
     * @param suffix
     * @param rootPath
     * @see Builder
     */
    public ServletContextTemplateLocator(int priority, String rootPath,
            String suffix) {
        this(priority, rootPath, suffix, null);
    }

    /**
     *
     * @param priority
     * @param suffix
     * @param rootPath
     * @param servletContext
     * @see Builder
     */
    public ServletContextTemplateLocator(int priority, String rootPath,
            String suffix, ServletContext servletContext) {
        super(priority, rootPath, suffix);
        this.servletContext = servletContext;
        checkRootPath();
    }

    @Override
    public Reader locate(String templatePath) {
        ServletContext ctx = getServletContext();
        if (ctx == null) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    "Servlet context not available");
        }

        String path = getRootPath() + addSuffix(toRealPath(templatePath));
        InputStream in = ctx.getResourceAsStream(path);

        if (in == null) {
            LOGGER.debug("Template not found: {}", path);
            return null;
        }
        LOGGER.debug("Template located: {}", templatePath);

        try {
            return new InputStreamReader(in, getDefaultFileEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
    }

    @Override
    public Set<String> getAllIdentifiers() {

        ServletContext ctx = getServletContext();

        if (ctx == null) {
            LOGGER.warn(
                    "Servlet context not available - cannot get all available identifiers");
            return Collections.emptySet();
        }

        Set<String> resources = listResources(getRootPath(), ctx);

        if (resources.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> identifiers = new HashSet<String>();
        for (String resource : resources) {
            String id = stripSuffix(constructVirtualPath(resource));
            identifiers.add(id);
            LOGGER.debug("Template available: {}", id);
        }
        return identifiers;
    }

    private Set<String> listResources(String path, ServletContext ctx) {

        Set<String> resources = new HashSet<String>();
        Set<String> resourcePaths = ctx.getResourcePaths(path);

        if (resourcePaths != null) {
            for (String resourcePath : resourcePaths) {
                if (resourcePath.endsWith(Strings.SLASH)) {
                    // Subdirectory
                    String subdirectory = getRootPath() + Strings
                            .substringAfter(resourcePath, getRootPath());
                    resources.addAll(listResources(subdirectory, ctx));
                } else {
                    if (getSuffix() != null
                            && !resourcePath.endsWith(getSuffix())) {
                        continue;
                    }
                    resources.add(resourcePath);
                }
            }
        }
        return resources;
    }

    private void checkRootPath() {
        if (getRootPath() == null || !getRootPath().startsWith(Strings.SLASH)) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
                    "A valid root path which begins with a slash must be set: "
                            + getRootPath());
        }
    }

    private ServletContext getServletContext() {

        if (servletContext != null) {
            return servletContext;
        }

        HttpServletRequest httpServletRequest = RequestHolder
                .getCurrentRequest();

        if (httpServletRequest != null) {
            return httpServletRequest.getServletContext();
        }
        return null;
    }

    @Override
    protected String constructVirtualPath(String source) {

        List<String> parts = Strings.split(
                Strings.substringAfter(source, getRootPath()), Strings.SLASH);

        StringBuilder name = new StringBuilder();
        for (Iterator<String> iterator = parts.iterator(); iterator
                .hasNext();) {
            name.append(iterator.next());
            if (iterator.hasNext()) {
                name.append(getVirtualPathSeparator());
            }
        }
        return name.toString();
    }

    /**
     *
     * @param priority
     * @return a new instance of builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @author Martin Kouba
     */
    public static class Builder {

        private ServletContext servletContext;

        private int priority;

        private String rootPath;

        private String suffix;

        private Builder() {
            this.rootPath = Strings.SLASH;
            this.priority = WithPriority.TEMPLATE_LOCATOR_DEFAULT_PRIORITY;
        }

        /**
         * @param servletContext
         *            the servletContext to set
         * @return self
         */
        public Builder setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
            return this;
        }

        /**
         * @param priority
         *            the priority to set
         * @return self
         */
        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         *
         * @param rootPath
         * @return self
         */
        public Builder setRootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        /**
         * If not set, a full template name must be used.
         *
         * @param suffix
         * @return self
         */
        public Builder setSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        /**
         *
         * @return
         */
        public ServletContextTemplateLocator build() {
            return new ServletContextTemplateLocator(priority, rootPath, suffix,
                    servletContext);
        }

    }

}
