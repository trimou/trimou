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
