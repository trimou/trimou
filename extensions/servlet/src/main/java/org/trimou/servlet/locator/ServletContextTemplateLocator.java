package org.trimou.servlet.locator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.locator.PathTemplateLocator;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.servlet.RequestHolder;
import org.trimou.util.Strings;

/**
 * Non-recursive servlet context template locator.
 *
 * @author Martin Kouba
 */
public class ServletContextTemplateLocator extends PathTemplateLocator {

	private static final Logger logger = LoggerFactory
			.getLogger(ServletContextTemplateLocator.class);

	private ServletContext servletContext;

	/**
	 *
	 * @param priority
	 * @param rootPath
	 */
	public ServletContextTemplateLocator(int priority, String rootPath) {
		super(priority, rootPath);
		checkRootPath();
	}

	/**
	 *
	 * @param priority
	 * @param rootPath
	 * @param servletContext
	 */
	public ServletContextTemplateLocator(int priority, String rootPath, ServletContext servletContext) {
		super(priority, rootPath);
		this.servletContext = servletContext;
		checkRootPath();
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 */
	public ServletContextTemplateLocator(int priority, String rootPath,
			String suffix) {
		super(priority, rootPath, suffix);
		checkRootPath();
	}

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 * @param servletContext
	 */
	public ServletContextTemplateLocator(int priority, String rootPath,
			String suffix, ServletContext servletContext) {
		super(priority, rootPath, suffix);
		this.servletContext = servletContext;
		checkRootPath();
	}

	@Override
	public Reader locate(String templateName) {

		ServletContext ctx = getServletContext();

		if (ctx == null) {
			throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
					"Servlet context not available outside HTTP request");
		}

		InputStream in = ctx.getResourceAsStream(getRootPath()
				+ addSuffix(templateName));

		return in != null ? new InputStreamReader(in) : null;
	}

	@Override
	public Set<String> getAllAvailableNames() {

		ServletContext ctx = getServletContext();

		if (ctx == null) {
			logger.warn("Servlet context not available - cannot get all available names");
			return Collections.emptySet();
		}

		Set<String> names = new HashSet<String>();
		Set<String> resources = ctx.getResourcePaths(getRootPath());

		for (String resource : resources) {

			String filename = StringUtils.removeStart(resource, getRootPath());

			if (getSuffix() != null) {
				if (resource.endsWith(getSuffix())) {
					names.add(stripSuffix(filename));
				}
			} else {
				names.add(filename);
			}
		}
		return names;
	}

	private void checkRootPath() {
		if (!getRootPath().startsWith(Strings.SLASH)) {
			throw new MustacheException(
					MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
					"Root path does not begin with slash: " + getRootPath());
		}
	}

	private ServletContext getServletContext() {

		if(this.servletContext != null) {
			return this.servletContext;
		}

		HttpServletRequest httpServletRequest = RequestHolder
				.getCurrentRequest();

		if (httpServletRequest != null) {
			return httpServletRequest.getServletContext();
		}
		return null;
	}

}
