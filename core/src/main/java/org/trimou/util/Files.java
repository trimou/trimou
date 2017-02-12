/*
 * Copyright 2015 Martin Kouba
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
package org.trimou.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class Files {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Files.class);

    private Files() {
    }

    /**
     *
     * @param dir
     * @return the list of matching files/templates
     */
    public static List<File> listFiles(File dir, String suffix) {

        List<File> files = new ArrayList<>();

        if (dir.isDirectory()) {
            final File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isDirectory()) {
                        files.addAll(listFiles(file, suffix));
                    } else if (file.isFile()) {
                        if (suffix != null && !file.getName().endsWith(suffix)) {
                            continue;
                        }
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    public static boolean isDirectoryUsable(File dir) {
        if (!dir.exists()) {
            LOGGER.warn("Dir not usable - does not exist: {}", dir);
            return false;
        }
        if (!dir.canRead()) {
            LOGGER.warn("Dir not usable - cannot read: {}", dir);
            return false;
        }
        if (!dir.isDirectory()) {
            LOGGER.warn("Dir not usable - not a directory: {}", dir);
            return false;
        }
        return true;
    }

    public static boolean isFileUsable(File file) {
        if (!file.exists()) {
            LOGGER.warn("File not usable - does not exist: {}", file);
            return false;
        }
        if (!file.canRead()) {
            LOGGER.warn("File not usable - cannot read: {}", file);
            return false;
        }
        if (!file.isFile()) {
            LOGGER.warn("File not usable - not a normal file: {}", file);
            return false;
        }
        return true;
    }

}
