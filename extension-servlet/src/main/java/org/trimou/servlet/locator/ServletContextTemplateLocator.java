package org.trimou.servlet.locator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.trimou.engine.locator.AbstractPathTemplateLocator;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.servlet.RequestHolder;
import org.trimou.util.Strings;

/**
 *
 * @author Martin Kouba
 */
public class ServletContextTemplateLocator extends AbstractPathTemplateLocator {

	/**
	 *
	 * @param priority
	 * @param suffix
	 * @param rootPath
	 */
	public ServletContextTemplateLocator(int priority, String suffix,
			String rootPath) {
		super(priority, suffix, rootPath);
		if (!rootPath.startsWith(Strings.SLASH)) {
			throw new MustacheException(
					MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
					"Root path does not begin with slash");
		}
	}

	@Override
	public Reader locate(String templateName) {

		InputStream in = RequestHolder
				.getCurrentRequest()
				.getServletContext()
				.getResourceAsStream(
						getRootPathname() + addSuffix(templateName));
		if (in == null) {
			return null;
		}
		return new InputStreamReader(in);
	}

	@Override
	public Set<String> getAllAvailableNames() {

		Set<String> names = new HashSet<String>();
		Set<String> resources = RequestHolder.getCurrentRequest()
				.getServletContext().getResourcePaths(getRootPathname());

		for (String resource : resources) {
			if (resource.endsWith(getSuffix())) {
				names.add(stripSuffix(StringUtils.removeStart(resource,
						getRootPathname())));
			}
		}
		return names;
	}

}
