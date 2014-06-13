package org.trimou.spring.web.view;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trimou.Mustache;
import org.trimou.spring.web.view.TrimouView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Minkyu Cho
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimouViewTest {
	@Mock
	private HttpServletResponse response;
	@Mock
	private PrintWriter writer;
	@Mock
	private Mustache template;

	@Test
	public void rendersAModelUsingItsTemplate() throws Exception {
		final Map<String, Object> model = Maps.newHashMap();

		HttpServletRequest UNUSED_REQUEST = null;

		when(response.getWriter()).thenReturn(writer);

		TrimouView view = new TrimouView();
		view.setTemplate(template);
		view.renderMergedTemplateModel(model, UNUSED_REQUEST, response);

		verify(response).setContentType(anyString());
		verify(template).render(writer, model);
		verify(writer).flush();
	}
}
