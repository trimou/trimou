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
package org.trimou;

import org.trimou.engine.id.Identified;

/**
 * A compiled mustache template. Implementation must be thread-safe.
 *
 * @author Martin Kouba
 */
public interface Mustache extends Identified {

    /**
     * A name is used to locate the template contents by means of
     * {@link org.trimou.engine.locator.TemplateLocator}. It's sometimes
     * referenced as an identifier provided by the user. In most cases, the name
     * represents a full (possibly virtual) path of the template.
     * <p>
     * Note that it's possible to have more than one template with the same name
     * for a {@link org.trimou.engine.MustacheEngine} instance, due to existence
     * of
     * {@link org.trimou.engine.MustacheEngine#compileMustache(String, String)}.
     *
     * @return the template name
     * @see #getGeneratedId()
     */
    String getName();

    /**
     * Render the template.
     *
     * @param data
     *            Optional context object (ideally immutable), may be
     *            <code>null</code>
     * @return the rendered template as string
     */
    String render(Object data);

    /**
     * Render the template.
     * <p>
     * Watch out! Any appendable-specific operations (e.g. stream flushing and
     * closing) are not performed automatically.
     * <p>
     * Note that if an asynchronous helper is used the "append" operations may
     * be delayed due to the use of an intermediate buffer.
     *
     * @param appendable
     *            The appendable to append the rendered template to
     * @param data
     *            Optional context object (ideally immutable), may be
     *            <code>null</code>
     */
    void render(Appendable appendable, Object data);

}
