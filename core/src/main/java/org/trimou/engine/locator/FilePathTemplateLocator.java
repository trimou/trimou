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

/**
 * Abstract file-based template locator.
 *
 * @author Martin Kouba
 */
public abstract class FilePathTemplateLocator extends PathTemplateLocator<File> {

	private static final Logger logger = LoggerFactory
			.getLogger(FilePathTemplateLocator.class);

	/**
	 *
	 * @param priority
	 * @param rootPath
	 * @param suffix
	 */
	public FilePathTemplateLocator(int priority, String rootPath, String suffix) {
		super(priority, rootPath, suffix);
	}

	/**
	 *
	 * @param priority
	 * @param rootPath
	 */
	public FilePathTemplateLocator(int priority, String rootPath) {
		super(priority, rootPath);
	}

	@Override
	public Reader locate(String filePath) {
		return locateRealPath(toRealPath(filePath));
	}

	@Override
	public Set<String> getAllIdentifiers() {

		List<File> files = listFiles(getRootDir());

		if (files.isEmpty()) {
			return Collections.emptySet();
		}

		Set<String> names = new HashSet<String>();
		for (File file : files) {
			if (isFileUsable(file)) {
				String name = stripSuffix(constructVirtualPath(file));
				names.add(name);
				logger.debug("Template name available: {}", name);
			}
		}
		return names;
	}

	@Override
	protected String constructVirtualPath(File source) {

		File rootDir = getRootDir();
		File parent = source.getParentFile();
		List<String> parts = new ArrayList<String>();

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
	 * @return
	 */
	protected abstract File getRootDir();

	/**
	 *
	 * @param realPath
	 * @return
	 */
	protected abstract Reader locateRealPath(String realPath);

	/**
	 *
	 * @param dir
	 * @return
	 */
	protected List<File> listFiles(File dir) {

		List<File> files = new ArrayList<File>();

		if (dir.isDirectory()) {

			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					files.addAll(listFiles(file));
				} else if (file.isFile()) {
					if (getSuffix() != null
							&& !file.getName().endsWith(getSuffix())) {
						continue;
					}
					files.add(file);
				}
			}
		}
		return files;
	}

	protected boolean isDirectoryUsable(File dir) {
		if (!dir.exists()) {
			logger.warn("Dir not usable - does not exist: {}", dir);
			return false;
		}
		if (!dir.canRead()) {
			logger.warn("Dir not usable - cannot read: {}", dir);
			return false;
		}
		if (!dir.isDirectory()) {
			logger.warn("Dir not usable - not a directory: {}", dir);
			return false;
		}
		return true;
	}

	protected boolean isFileUsable(File file) {
		if (!file.exists()) {
			logger.warn("File not usable - does not exist: {}", file);
			return false;
		}
		if (!file.canRead()) {
			logger.warn("File not usable - cannot read: {}", file);
			return false;
		}
		if (!file.isFile()) {
			logger.warn("File not usable - not a normal file: {}", file);
			return false;
		}
		return true;
	}

	protected void checkRootDir() {
		File rootDir = getRootDir();
		if (!isDirectoryUsable(rootDir)) {
			throw new MustacheException(
					MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
					"Invalid root dir: %s", rootDir);
		}
	}

}
