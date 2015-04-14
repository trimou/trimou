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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Files;
import org.trimou.util.Strings;

import com.google.common.collect.ImmutableSet;

/**
 * Classpath template locator. There is a special {@link Builder} for
 * convenience.
 *
 * Note that for a WAR archive, the classpath root is WEB-INF/classes (other
 * parts of the archive are not available). Consider using
 * <code>org.trimou.servlet.locator.ServletContextTemplateLocator</code> from
 * the trimou-extension-servlet.
 *
 * By default, the locator will attempt to scan the classpath to get all
 * available template identifiers. However, only roots representing a directory
 * on a filesystem will be processed. The scanning of JARs and other sources is
 * not trivial. Moreover, it's legal for JARs to have no directory entries at
 * all. Scanning can be entirely disabled - see also
 * {@link Builder#setScanClasspath(boolean)}.
 *
 * @author Martin Kouba
 */
public class ClassPathTemplateLocator extends PathTemplateLocator<String> {

    private static final Logger logger = LoggerFactory
            .getLogger(ClassPathTemplateLocator.class);

    private final ClassLoader classLoader;

    private final boolean scanClasspath;

    /**
     *
     * @param priority
     * @param rootPath
     *            If null, no templates will be available for precompilation
     */
    public ClassPathTemplateLocator(int priority, String rootPath) {
        this(priority, rootPath, null, null, true);
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
        this(priority, rootPath, suffix, null, true);
    }

    /**
     *
     * @param priority
     * @param rootPath
     *            If null, no templates will be available for precompilation
     * @param classLoader
     *            If null, use the TCCL or the CL of this class
     */
    public ClassPathTemplateLocator(int priority, String rootPath,
            ClassLoader classLoader) {
        this(priority, rootPath, null, classLoader, true);
    }

    /**
     *
     * @param priority
     * @param suffix
     *            If null, a full template name must be used
     * @param rootPath
     *            If null, no templates will be available for precompilation
     * @param classLoader
     *            If null, use the TCCL or the CL of this class
     * @param scanClasspath
     *            If set to <code>true</code> the locator will attempt to scan
     *            the classpath to get all available template identifiers.
     */
    public ClassPathTemplateLocator(int priority, String rootPath,
            String suffix, ClassLoader classLoader, boolean scanClasspath) {
        super(priority, rootPath, suffix);
        if (classLoader == null) {
            classLoader = SecurityActions.getContextClassLoader();
            if (classLoader == null) {
                classLoader = SecurityActions
                        .getClassLoader(ClassPathTemplateLocator.class);
            }
        }
        this.classLoader = classLoader;
        this.scanClasspath = scanClasspath;
    }

    @Override
    public Reader locate(String templateId) {
        return locateRealPath(toRealPath(templateId));
    }

    @Override
    public Set<String> getAllIdentifiers() {

        if (!scanClasspath || getRootPath() == null) {
            return Collections.emptySet();
        }

        ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        try {
            // Find all roots
            Enumeration<URL> resources = classLoader
                    .getResources(getRootPath());

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (Strings.URL_PROCOTOL_FILE.equals(resource.getProtocol())) {
                    // Right now only files are supported
                    try {
                        File root = Paths.get(resource.toURI()).toFile();
                        if (!Files.isDirectoryUsable(root)) {
                            continue;
                        }
                        List<File> files = Files.listFiles(root, getSuffix());
                        if (!files.isEmpty()) {
                            for (File file : files) {
                                if (Files.isFileUsable(file)) {
                                    String id = stripSuffix(constructVirtualPath(
                                            root, file));
                                    builder.add(id);
                                    logger.debug("Template available: {}", id);
                                }
                            }
                        }
                    } catch (URISyntaxException e) {
                        logger.warn("Unable to process root path: {}",
                                resource, e);
                    }
                } else {
                    logger.debug(
                            "Protocol not supported - root resource is ignored: {}",
                            resource);
                }
            }
        } catch (IOException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
        return builder.build();
    }

    @Override
    protected String constructVirtualPath(String source) {
        // Retain for backwards compatibility
        throw new UnsupportedOperationException();
    }

    private Reader locateRealPath(String realPath) {

        final String name = getRootPath() != null ? getRootPath()
                + addSuffix(realPath) : addSuffix(realPath);
        Reader reader = null;

        try {
            Enumeration<URL> resources = classLoader.getResources(name);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (reader != null) {
                    logger.warn("Another/duplicit template for {} ignored: {}",
                            name, resource);
                } else {
                    reader = new InputStreamReader(resource.openStream(),
                            getDefaultFileEncoding());
                    logger.debug("Template {} located: {}", name, resource);
                }
            }
        } catch (IOException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
        return reader;
    }

    private String constructVirtualPath(File root, File source) {

        File parent = source.getParentFile();
        List<String> parts = new ArrayList<String>();

        if (parent == null) {
            throw new IllegalStateException(
                    "Unable to construct virtual path - no parent directory found");
        }
        parts.add(source.getName());

        while (!root.equals(parent)) {
            parts.add(parent.getName());
            parent = parent.getParentFile();
        }
        Collections.reverse(parts);
        StringBuilder name = new StringBuilder();
        for (Iterator<String> iterator = parts.iterator(); iterator.hasNext();) {
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

        private boolean scanClasspath = true;

        private Builder(int priority) {
            this.priority = priority;
        }

        /**
         * If not set, use the TCCL or the CL of this class.
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
         * If set to <code>true</code> the locator will attempt to scan the
         * classpath to get all available template identifiers.
         *
         * @param scanClasspath
         * @return builder
         * @see TemplateLocator#getAllIdentifiers()
         */
        public Builder setScanClasspath(boolean scanClasspath) {
            this.scanClasspath = scanClasspath;
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
            ClassLoader cl;
            if (classLoader != null) {
                cl = classLoader;
            } else {
                cl = SecurityActions.getContextClassLoader();
                if (cl == null) {
                    cl = ClassPathTemplateLocator.class.getClassLoader();
                }
            }
            return new ClassPathTemplateLocator(priority, rootPath, suffix, cl,
                    scanClasspath);
        }

    }

}
