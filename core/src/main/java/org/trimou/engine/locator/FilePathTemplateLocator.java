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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Files;

/**
 * Abstract file-based template locator.
 *
 * @author Martin Kouba
 */
public abstract class FilePathTemplateLocator extends PathTemplateLocator<File> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FilePathTemplateLocator.class);

    /**
     *
     * @param priority
     * @param rootPath
     */
    public FilePathTemplateLocator(int priority, String rootPath) {
        this(priority, rootPath, null);
    }

    /**
     *
     * @param priority
     * @param rootPath
     * @param suffix
     */
    public FilePathTemplateLocator(int priority, String rootPath, String suffix) {
        super(priority, rootPath, suffix);
    }

    @Override
    public Reader locate(String filePath) {
        return locateRealPath(toRealPath(filePath));
    }

    @Override
    public Set<String> getAllIdentifiers() {

        List<File> files = Files.listFiles(getRootDir(), getSuffix());

        if (files.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> identifiers = new HashSet<>();
        for (File file : files) {
            if (Files.isFileUsable(file)) {
                String id = stripSuffix(constructVirtualPath(file));
                identifiers.add(id);
                LOGGER.debug("Template available: {}", id);
            }
        }
        return identifiers;
    }

    @Override
    protected String constructVirtualPath(File source) {

        File rootDir = getRootDir();
        File parent = source.getParentFile();
        List<String> parts = new ArrayList<>();

        if (parent == null) {
            throw new IllegalStateException(
                    "Unable to construct virtual path - no parent directory found");
        }
        parts.add(source.getName());

        while (!rootDir.equals(parent)) {
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
     * @return the root directory
     * @see PathTemplateLocator#getRootPath()
     */
    protected abstract File getRootDir();

    /**
     *
     * @param realPath
     * @return the reader for the given path
     */
    protected abstract Reader locateRealPath(String realPath);

    protected void checkRootDir() {
        File rootDir = getRootDir();
        if(rootDir == null) {
            return;
        }
        if (!Files.isDirectoryUsable(rootDir)) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
                    "Invalid root dir: %s", rootDir);
        }
    }

}
