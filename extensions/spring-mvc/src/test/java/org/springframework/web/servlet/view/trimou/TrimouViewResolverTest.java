package org.springframework.web.servlet.view.trimou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.trimou.TrimouViewResolver;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 *
 * @author Minkyu Cho
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimouViewResolverTest {

	@Mock
	private Mustache mustache;
	@Mock
	private MustacheEngine engine;
	@Mock
	private ServletContext servletContext;

	/**
	 * A basic test where no prefix is used.
	 */
	@Test
	public void resolvesViewWithoutPrefix() throws Exception {
		final String viewPath = "top-level.mustache";
		when(engine.getMustache(viewPath)).thenReturn(mustache);

		TrimouViewResolver sut = new TrimouViewResolver();
		sut.setServletContext(servletContext);
        sut.setPrefix("/");
        sut.afterPropertiesSet();
        sut.setEngine(engine);

		AbstractUrlBasedView view = sut.buildView(viewPath);
		assertThat(view, is(notNullValue()));
	}

	/**
	 * Ensure the prefix is passed on to the template loader
	 * and that the template loader is called with a fully 
	 * resolved view path.
	 */
	@Test
	public void resolvesViewWithPrefix() throws Exception {
		final String viewPath = "/WEB-INF/views/";
		final String viewName = "hello.mustache";

		when(engine.getMustache(viewPath + viewName)).thenReturn(mustache);

		TrimouViewResolver sut = new TrimouViewResolver();
		sut.setServletContext(servletContext);
		sut.setPrefix(viewPath);
		sut.afterPropertiesSet();
		sut.setEngine(engine);

		AbstractUrlBasedView view = sut.buildView(viewName);
		assertThat(view, is(notNullValue()));
	}
}
