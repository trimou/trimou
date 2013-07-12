package org.trimou.servlet.resolver;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Martin Kouba
 */
public class HttpServletRequestWrapper {

    private final HttpServletRequest request;

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
     * @see HttpServletRequest#getAuthType()
     */
    public String getAuthType() {
        return request.getAuthType();
    }

    /**
     * @return {@link HttpServletRequest#getCookies()}
     */
    public Cookie[] getCookies() {
        return request.getCookies();
    }

    /**
     * @return {@link ServletRequest#getRemoteAddr()}
     */
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    /**
     * @return {@link ServletRequest#getContentLength()}
     */
    public int getContentLength() {
        return request.getContentLength();
    }

    /**
     * @return {@link ServletRequest#getContentType()}
     */
    public String getContentType() {
        return request.getContentType();
    }

    /**
     * @return {@link ServletRequest#getCharacterEncoding()}
     */
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    /**
     * @return {@link ServletRequest#getLocale()}
     */
    public Locale getLocale() {
        return request.getLocale();
    }

    /**
     * @return {@link ServletRequest#getScheme()}
     */
    public String getScheme() {
        return request.getScheme();
    }

    /**
     * @see ServletRequest#getServerPort()
     */
    public int getServerPort() {
        return request.getServerPort();
    }

    /**
     * @see ServletRequest#getServerName()
     */
    public String getServerName() {
        return request.getServerName();
    }

}
