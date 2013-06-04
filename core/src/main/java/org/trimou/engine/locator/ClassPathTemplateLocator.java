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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Non-recursive classpath template locator.
 *
 * Note that for WAR archive, the classpath root is WEB-INF/classes (other parts
 * of the archive are not available).
 *
 * @author Martin Kouba
 */
public class ClassPathTemplateLocator extends PathTemplateLocator {

	private ClassLoader classLoader;

	/**
	 *
	 * @param priority
	 * @param rootPath
	 */
	public ClassPathTemplateLocator(int priority, String rootPath) {
		super(priority, rootPath);
		this.classLoader = Thread.currentThread().getContextClassLoader();
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 */
	public ClassPathTemplateLocator(int priority, String rootPath, String suffix) {
		super(priority, rootPath, suffix);
		this.classLoader = Thread.currentThread().getContextClassLoader();
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPathname
	 * @param classLoader
	 */
	private ClassPathTemplateLocator(int priority, String rootPath,
			String suffix, ClassLoader classLoader) {
		super(priority, rootPath, suffix);
		this.classLoader = classLoader;
	}

	/**
	 *
	 * @param priority
	 * @param rootPathname
	 * @param classLoader
	 */
	private ClassPathTemplateLocator(int priority, String rootPath,
			ClassLoader classLoader) {
		super(priority, rootPath);
		this.classLoader = classLoader;
	}

	@Override
	public Reader locate(String templateName) {
		InputStream in = classLoader.getResourceAsStream(getRootPath()
				+ addSuffix(templateName));
		if (in == null) {
			return null;
		}
		return new InputStreamReader(in);
	}

	@Override
	public Set<String> getAllAvailableNames() {

		Set<String> names = new HashSet<String>();

		try {

			Enumeration<URL> resources = classLoader
					.getResources(getRootPath());

			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				String urlPath = url.getFile();
				urlPath = URLDecoder.decode(urlPath, "UTF-8");
				if (urlPath.startsWith("file:")) {
					urlPath = urlPath.substring(5);
				}
				if (urlPath.indexOf('!') > 0) {
					urlPath = urlPath.substring(0, urlPath.indexOf('!'));
				}
				File file = new File(urlPath);

				if (file.isDirectory()) {
					for (File found : file.listFiles()) {

						if (found.isFile()) {
							if (getSuffix() != null) {
								if (found.getName().endsWith(getSuffix())) {
									names.add(stripSuffix(found.getName()));
								}
							} else {
								names.add(found.getName());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
					e);
		}
		return names;
	}

}
