/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
