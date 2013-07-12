package org.trimou.servlet.resolver;

import javax.servlet.http.HttpSession;

/**
 *
 * @author Martin Kouba
 */
public class HttpSessionWrapper {

    private final HttpSession session;

    /**
     *
     * @param session
     */
    protected HttpSessionWrapper(HttpSession session) {
        super();
        this.session = session;
    }

    /**
     * @see HttpSession#getId()
     */
    public String getId() {
        return session.getId();
    }

    /**
     * @see HttpSession#getCreationTime()
     */
    public long getCreationTime() {
        return session.getCreationTime();
    }

    /**
     * @see HttpSession#getLastAccessedTime()
     */
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    /**
     * @see HttpSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    /**
     * @return {@link HttpSession#isNew()}
     */
    public boolean isNew() {
        return session.isNew();
    }
}
