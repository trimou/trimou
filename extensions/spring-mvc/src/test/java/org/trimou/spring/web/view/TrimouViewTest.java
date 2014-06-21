package org.trimou.spring.web.view;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;

import com.google.common.collect.Maps;

/**
 * @author Minkyu Cho
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimouViewTest {
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    @Mock
    private MustacheEngine engine;
    @Mock
    private Mustache template;

    @Test
    public void rendersAModelUsingItsTemplate() throws Exception {
        //given
        final Map<String, Object> model = Maps.newHashMap();
        final String viewName = "foo.mustache";

        HttpServletRequest UNUSED_REQUEST = null;

        //when
        when(engine.getMustache(viewName)).thenReturn(template);
        when(response.getWriter()).thenReturn(writer);

        TrimouView view = new TrimouView();
        view.setEngine(engine);
        view.setViewName(viewName);
        view.renderMergedTemplateModel(model, UNUSED_REQUEST, response);

        //then
        verify(response).setContentType(anyString());
        verify(template).render(writer, model);
        verify(writer).flush();
    }
}
