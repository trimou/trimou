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

import org.apache.commons.lang3.StringUtils;
import org.trimou.util.Strings;

/**
 *
 * @author Martin Kouba
 */
public abstract class PathTemplateLocator implements TemplateLocator {

	private String pathSeparator;

	private int priority;

	private String suffix;

	private String rootPath;

	/**
	 *
	 * @param priority
	 * @param rootPath
	 */
	public PathTemplateLocator(int priority, String rootPath) {
		super();
		this.pathSeparator = getPathSeparator();
		this.priority = priority;
		this.suffix = null;
		initRootPath(rootPath);
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 */
	public PathTemplateLocator(int priority, String rootPath,
			String suffix) {
		super();
		this.pathSeparator = getPathSeparator();
		this.priority = priority;
		this.suffix = suffix;
		initRootPath(rootPath);
	}

	private void initRootPath(String rootPath) {
		this.rootPath = rootPath.endsWith(pathSeparator) ? rootPath
				: (rootPath + pathSeparator);
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getRootPath() {
		return rootPath;
	}

	public String stripSuffix(String filename) {
		return suffix != null ? StringUtils.stripEnd(filename, "." + suffix)
				: filename;
	}

	public String addSuffix(String filename) {
		return suffix != null ? (filename + "." + suffix) : filename;
	}

	protected String getPathSeparator() {
		return Strings.SLASH;
	}

	@Override
	public String toString() {
		return String.format("%s [priority: %s, suffix: %s, rootPath: %s]",
				getClass().getName(), getPriority(), getSuffix(),
				getRootPath());
	}

}
