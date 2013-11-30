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

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.util.Strings;

/**
 * Represents a template locator where the template identifier is a path.
 *
 * @author Martin Kouba
 * @param <T>
 *            the source of the template path (e.g. File, String,...)
 */
public abstract class PathTemplateLocator<T> extends AbstractTemplateLocator {

    private static final Logger logger = LoggerFactory
            .getLogger(PathTemplateLocator.class);

    /**
     * Virtual path separator
     */
    public static final ConfigurationKey VIRTUAL_PATH_SEPARATOR_KEY = new SimpleConfigurationKey(
            PathTemplateLocator.class.getName() + ".virtualPathSeparator",
            Strings.SLASH);

    private final String suffix;

    private final String rootPath;

    private final String realPathSeparator;

    private String virtualPathSeparator;

    private String defaultFileEncoding;

    /**
     *
     * @param priority
     * @param rootPath
     */
    public PathTemplateLocator(int priority, String rootPath) {
        super(priority);
        this.realPathSeparator = getRealPathSeparator();
        this.suffix = null;
        this.rootPath = initRootPath(rootPath);
    }

    /**
     *
     * @param priority
     * @param suffix
     * @param rootPath
     */
    public PathTemplateLocator(int priority, String rootPath, String suffix) {
        super(priority);
        this.realPathSeparator = getRealPathSeparator();
        this.suffix = suffix;
        this.rootPath = initRootPath(rootPath);
    }

    @Override
    public void init(Configuration configuration) {
        this.virtualPathSeparator = configuration
                .getStringPropertyValue(VIRTUAL_PATH_SEPARATOR_KEY);
        this.defaultFileEncoding = configuration
                .getStringPropertyValue(EngineConfigurationKey.DEFAULT_FILE_ENCODING);
        logger.info(
                "{} initialized [virtualPathSeparator: {}, defaultFileEncoding: {}]",
                getClass().getSimpleName(), getVirtualPathSeparator(),
                getDefaultFileEncoding());
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(VIRTUAL_PATH_SEPARATOR_KEY);
    }

    public String getSuffix() {
        return suffix;
    }

    public String getRootPath() {
        return rootPath;
    }

    protected String stripSuffix(String filename) {
        return suffix != null ? StringUtils.removeEnd(filename, "." + suffix)
                : filename;
    }

    protected String addSuffix(String filename) {
        return suffix != null ? (filename + "." + suffix) : filename;
    }

    protected String getRealPathSeparator() {
        return Strings.SLASH;
    }

    protected String getVirtualPathSeparator() {
        return virtualPathSeparator;
    }

    protected String getDefaultFileEncoding() {
        return defaultFileEncoding;
    }

    /**
     * @param source
     * @return the virtual path of the template
     */
    protected abstract String constructVirtualPath(T source);

    /**
     *
     * @param virtualPath
     * @return the real path
     */
    protected String toRealPath(String virtualPath) {

        String[] parts = StringUtils.split(virtualPath,
                getVirtualPathSeparator());

        StringBuilder realPath = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            realPath.append(parts[i]);
            if (i + 1 < parts.length) {
                realPath.append(getRealPathSeparator());
            }
        }
        return realPath.toString();
    }

    private String initRootPath(String rootPath) {
        return rootPath.endsWith(realPathSeparator) ? rootPath
                : (rootPath + realPathSeparator);
    }

    @Override
    public String toString() {
        return String
                .format("%s [priority: %s, suffix: %s, rootPath: %s]",
                        getClass().getName(), getPriority(), getSuffix(),
                        getRootPath());
    }

}
