package org.trimou.servlet.resolver;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Martin Kouba
 */
public class HttpServletRequestWrapper {

	private HttpServletRequest request;

	/**
	 * 
	 * @param request
	 */
	protected HttpServletRequestWrapper(HttpServletRequest request) {
		super();
		this.request = request;
	}

	/**
	 * @see HttpServletRequest#getMethod()
	 */
	public String getMethod() {
		return request.getMethod();
	}

	/**
	 * @see HttpServletRequest#getContextPath()
	 */
	public String getContextPath() {
		return request.getContextPath();
	}

	/**
	 * @see HttpServletRequest#getQueryString()
	 */
	public String getQueryString() {
		return request.getQueryString();
	}

	/**
	 * @see HttpServletRequest#getSession(boolean)
	 */
	public HttpSessionWrapper getSessionIfExists() {
		return new HttpSessionWrapper(request.getSession(false));
	}

	/**
	 * @see HttpServletRequest#getServerPort()
	 */
	public int getServerPort() {
		return request.getServerPort();
	}

	/**
	 * @see HttpServletRequest#getServerName()
	 */
	public String getServerName() {
		return request.getServerName();
	}

	/**
	 * @see HttpServletRequest#getAuthType()
	 */
	public String getAuthType() {
		return request.getAuthType();
	}

}
