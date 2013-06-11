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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Classpath template locator.
 *
 * Note that for WAR archive, the classpath root is WEB-INF/classes (other parts
 * of the archive are not available).
 *
 * @author Martin Kouba
 */
public class ClassPathTemplateLocator extends FilePathTemplateLocator {

	private static final Logger logger = LoggerFactory
			.getLogger(ClassPathTemplateLocator.class);

	private ClassLoader classLoader;

	/**
	 *
	 * @param priority
	 * @param rootPath
	 */
	public ClassPathTemplateLocator(int priority, String rootPath) {
		this(priority, rootPath, Thread.currentThread().getContextClassLoader());
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 */
	public ClassPathTemplateLocator(int priority, String rootPath, String suffix) {
		this(priority, rootPath, suffix, Thread.currentThread()
				.getContextClassLoader());
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
		checkRootDir();
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
		checkRootDir();
	}

	@Override
	public Reader locateRealPath(String realPath) {
		InputStream in = classLoader.getResourceAsStream(getRootPath()
				+ addSuffix(realPath));
		if (in == null) {
			return null;
		}
		logger.debug("Template located: {}", getRootPath() + realPath);
		return new InputStreamReader(in);
	}

	@Override
	protected File getRootDir() {

		try {

			URL url = classLoader.getResource(getRootPath());

			if (url == null) {
				throw new MustacheException(
						MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
						"Root path resource not found: %s", getRootPath());
			}
			return new File(URLDecoder.decode(url.getFile(), "UTF-8"));

		} catch (UnsupportedEncodingException e) {
			throw new MustacheException(
					MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION, e);
		}
	}

}
