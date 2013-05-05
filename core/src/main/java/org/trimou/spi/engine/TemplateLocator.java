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
package org.trimou.spi.engine;

import java.io.Reader;
import java.util.Set;

import org.trimou.api.engine.Configuration;
import org.trimou.spi.WithPriority;

/**
 * Automatically locate the template contents for the given template name.
 *
 * Implementation must be thread-safe.
 *
 * @author Martin Kouba
 */
public interface TemplateLocator extends WithPriority {

	public static final int DEFAULT_SYSTEM_TEMPLATE_LOCATOR_PRIORITY = 10;

	/**
	 * @param templateName
	 *            The template name
	 * @return the reader object for a template with the given name or
	 *         <code>null</code> if no available template with the given name
	 *         exists
	 */
	public Reader locate(String templateName);

	/**
	 *
	 * @return the set of names of all available templates
	 * @see Configuration#isPrecompileAllTemplates()
	 */
	public Set<String> getAllAvailableNames();

}
