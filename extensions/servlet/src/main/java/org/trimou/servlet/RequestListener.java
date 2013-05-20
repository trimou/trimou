package org.trimou.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Martin Kouba
 */
@WebListener
public class RequestListener implements ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent event) {
		if (event.getServletRequest() instanceof HttpServletRequest) {
			RequestHolder.requestInitialized((HttpServletRequest) event
					.getServletRequest());
		} else {
			throw new IllegalStateException(
					"This extension only supports HTTP Servlet lifecycle");
		}

	}

	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		RequestHolder.requestDestroyed();
	}
}
