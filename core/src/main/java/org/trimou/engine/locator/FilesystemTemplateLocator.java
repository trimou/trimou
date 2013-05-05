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
import java.io.FilenameFilter;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.trimou.MustacheException;
import org.trimou.MustacheProblem;
import org.trimou.util.Strings;

/**
 * Non-recursive filesystem template locator.
 *
 * @author Martin Kouba
 */
public class FilesystemTemplateLocator extends AbstractPathTemplateLocator {

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPathname
	 */
	public FilesystemTemplateLocator(int priority, String suffix,
			String rootPathname) {
		super(priority, suffix, rootPathname);

		File rootDir = new File(rootPathname);

		if (!rootDir.exists() || !rootDir.canRead() || !rootDir.isDirectory()) {
			throw new MustacheException(
					MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
					"Invalid root dir: " + rootDir);
		}
	}

	@Override
	public Reader locate(String templateName) {
		try {

			File templateFile = new File(new File(getRootPathname()),
					addSuffix(templateName));

			if (!templateFile.exists() || !templateFile.canRead()
					|| !templateFile.isFile()) {
				return null;
			}
			return new FileReader(templateFile);

		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public Set<String> getAllAvailableNames() {

		File rootDir = new File(getRootPathname());
		File[] files = rootDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(getSuffix());
			}
		});

		if (files == null || files.length < 1) {
			return Collections.emptySet();
		}

		Set<String> names = new HashSet<String>();
		for (File file : files) {
			if (file.isFile()) {
				names.add(stripSuffix(file.getName()));
			}
		}
		return names;
	}

	@Override
	protected String getPathSeparator() {
		return Strings.FILE_SEPARATOR;
	}

}
