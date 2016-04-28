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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;
import org.trimou.util.Files;
import org.trimou.util.Strings;

/**
 * Filesystem template locator.
 *
 * @author Martin Kouba
 */
public class FileSystemTemplateLocator extends FilePathTemplateLocator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileSystemTemplateLocator.class);

    /**
     *
     * @param priority
     * @param rootPath
     * @see Builder
     */
    public FileSystemTemplateLocator(int priority, String rootPath) {
        this(priority, rootPath, null);
    }

    /**
     *
     * @param priority
     * @param rootPath
     * @param suffix
     * @see Builder
     */
    public FileSystemTemplateLocator(int priority, String rootPath,
            String suffix) {
        super(priority, rootPath, suffix);
        Checker.checkArgumentNotEmpty(rootPath);
        checkRootDir();
    }

    @Override
    public Reader locateRealPath(String realPath) {
        try {
            File template = new File(new File(getRootPath()),
                    addSuffix(realPath));
            if (!Files.isFileUsable(template)) {
                return null;
            }
            LOGGER.debug("Template located: {}", template.getAbsolutePath());
            return new InputStreamReader(new FileInputStream(template),
                    getDefaultFileEncoding());
        } catch (FileNotFoundException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
    }

    @Override
    protected String getRealPathSeparator() {
        return Strings.FILE_SEPARATOR;
    }

    @Override
    protected File getRootDir() {
        return new File(getRootPath());
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

        private int priority;

        private String rootPath;

        private String suffix;

        private Builder() {
            this.priority = TemplateLocator.DEFAULT_PRIORITY;
        }

        /**
         * @param priority
         *            the priority to set
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
        public FileSystemTemplateLocator build() {
            return new FileSystemTemplateLocator(priority, rootPath, suffix);
        }

    }

}
