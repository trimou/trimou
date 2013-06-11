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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.util.Strings;

/**
 * Filesystem template locator.
 *
 * @author Martin Kouba
 */
public class FileSystemTemplateLocator extends FilePathTemplateLocator {

	private static final Logger logger = LoggerFactory
			.getLogger(FileSystemTemplateLocator.class);

	/**
	 *
	 * @param priority
	 * @param rootPath
	 */
	public FileSystemTemplateLocator(int priority, String rootPath) {
		super(priority, rootPath);
		checkRootDir();
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 */
	public FileSystemTemplateLocator(int priority, String rootPath,
			String suffix) {
		super(priority, rootPath, suffix);
		checkRootDir();
	}

	@Override
	public Reader locateRealPath(String realPath) {
		try {

			File templateFile = new File(new File(getRootPath()),
					addSuffix(realPath));

			if (!isFileUsable(templateFile)) {
				return null;
			}
			logger.debug("Template located: {}", templateFile.getAbsolutePath());
			return new FileReader(templateFile);

		} catch (FileNotFoundException e) {
			return null;
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

}
