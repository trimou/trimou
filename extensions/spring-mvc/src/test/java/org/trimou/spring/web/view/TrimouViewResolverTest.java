package org.trimou.spring.web.view;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.exception.MustacheException;

/**
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
     * When the prefix did not set, then viewResolver throws the NullPointerException.
     */
    @Test(expected = MustacheException.class)
    public void resolvesViewWithoutPrefix() throws Exception {
        //given
        final String viewName = "top-level.mustache";

        //when
        when(engine.getMustache(viewName)).thenReturn(mustache);

        TrimouViewResolver sut = new TrimouViewResolver();
        sut.setServletContext(servletContext);
        sut.afterPropertiesSet();
        sut.setEngine(engine);

        //then
        AbstractUrlBasedView view = sut.buildView(viewName);
        assertThat(view, is(notNullValue()));
    }

    /**
     * When not valid prefix did set, then viewResolver throws the MustacheException.
     */
    @Test(expected = MustacheException.class)
    public void resolvesViewWithNotValidPrefix() throws Exception {
        //given
        final String viewPath = "WEB-INF/views/";
        final String viewName = "top-level.mustache";

        //when
        when(engine.getMustache(viewName)).thenReturn(mustache);

        TrimouViewResolver sut = new TrimouViewResolver();
        sut.setServletContext(servletContext);
        sut.setPrefix(viewPath);
        sut.afterPropertiesSet();
        sut.setEngine(engine);

        //then
        AbstractUrlBasedView view = sut.buildView(viewName);
        assertThat(view, is(notNullValue()));
    }

    /**
     * Ensure the prefix is passed on to the template loader
     * and that the template loader is called with a fully
     * resolved view path.
     */
    @Test
    public void resolvesViewWithPrefix() throws Exception {
        //given
        final String viewPath = "/WEB-INF/views/";
        final String viewName = "hello.mustache";

        //when
        when(engine.getMustache(viewPath + viewName)).thenReturn(mustache);

        TrimouViewResolver sut = new TrimouViewResolver();
        sut.setServletContext(servletContext);
        sut.setPrefix(viewPath);
        sut.afterPropertiesSet();
        sut.setEngine(engine);

        //then
        AbstractUrlBasedView view = sut.buildView(viewName);
        assertThat(view, is(notNullValue()));
    }
}
