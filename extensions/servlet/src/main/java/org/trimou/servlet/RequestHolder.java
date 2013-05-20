package org.trimou.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple {@link ThreadLocal} based request holder.
 *
 * This holder might not be working correctly in case of the request is not
 * processed in one thread only (e.g. async servlets processing).
 *
 * @author Martin Kouba
 */
public final class RequestHolder {

	private static final ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<HttpServletRequest>();

	public static void requestInitialized(HttpServletRequest request) {
		CURRENT_REQUEST.set(request);
	}

	public static HttpServletRequest getCurrentRequest() {
		return CURRENT_REQUEST.get();
	}

	public static void requestDestroyed() {
		CURRENT_REQUEST.remove();
	}

}
