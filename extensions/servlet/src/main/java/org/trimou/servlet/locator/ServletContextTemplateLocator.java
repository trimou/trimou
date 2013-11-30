package org.trimou.servlet.locator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
 * Servlet context template locator.
 *
 * @author Martin Kouba
 */
public class ServletContextTemplateLocator extends PathTemplateLocator<String> {

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
    public ServletContextTemplateLocator(int priority, String rootPath,
            ServletContext servletContext) {
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
    public Reader locate(String templatePath) {

        ServletContext ctx = getServletContext();

        if (ctx == null) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    "Servlet context not available");
        }

        InputStream in = ctx.getResourceAsStream(getRootPath()
                + addSuffix(toRealPath(templatePath)));

        if (in == null) {
            return null;
        }
        logger.debug("Template located: {}", templatePath);

        try {
            return new InputStreamReader(in, getDefaultFileEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR, e);
        }
    }

    @Override
    public Set<String> getAllIdentifiers() {

        ServletContext ctx = getServletContext();

        if (ctx == null) {
            logger.warn("Servlet context not available - cannot get all available identifiers");
            return Collections.emptySet();
        }

        Set<String> resources = listResources(getRootPath(), ctx);

        if (resources.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> identifiers = new HashSet<String>();
        for (String resource : resources) {
            String id = stripSuffix(constructVirtualPath(resource));
            identifiers.add(id);
            logger.debug("Template available: {}", id);
        }
        return identifiers;
    }

    private Set<String> listResources(String path, ServletContext ctx) {

        Set<String> resources = new HashSet<String>();
        Set<String> resourcePaths = ctx.getResourcePaths(path);

        if (resourcePaths != null) {
            for (String resourcePath : resourcePaths) {
                if (resourcePath.endsWith(Strings.SLASH)) {
                    // Subdirectory
                    String subdirectory = getRootPath()
                            + StringUtils.substringAfter(resourcePath,
                                    getRootPath());
                    resources.addAll(listResources(subdirectory, ctx));
                } else {
                    if (getSuffix() != null
                            && !resourcePath.endsWith(getSuffix())) {
                        continue;
                    }
                    resources.add(resourcePath);
                }
            }
        }
        return resources;
    }

    private void checkRootPath() {
        if (!getRootPath().startsWith(Strings.SLASH)) {
            throw new MustacheException(
                    MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
                    "Root path does not begin with slash: " + getRootPath());
        }
    }

    private ServletContext getServletContext() {

        if (servletContext != null) {
            return servletContext;
        }

        HttpServletRequest httpServletRequest = RequestHolder
                .getCurrentRequest();

        if (httpServletRequest != null) {
            return httpServletRequest.getServletContext();
        }
        return null;
    }

    @Override
    protected String constructVirtualPath(String source) {

        String[] parts = StringUtils.split(
                StringUtils.substringAfter(source, getRootPath()),
                Strings.SLASH);

        StringBuilder name = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            name.append(parts[i]);
            if (i + 1 < parts.length) {
                name.append(getVirtualPathSeparator());
            }
        }
        return name.toString();
    }

}
