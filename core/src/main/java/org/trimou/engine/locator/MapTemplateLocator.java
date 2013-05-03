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
package org.trimou.engine.locator;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

import org.trimou.spi.engine.TemplateLocator;
import org.trimou.util.Priorities;

/**
 * Template locator backed by a {@link Map}.
 *
 * @author Martin Kouba
 */
public class MapTemplateLocator implements TemplateLocator {

	private int priority;

	/**
	 * Name to contents
	 */
	private Map<String, String> templates;

	public MapTemplateLocator(int priority, Map<String, String> templates) {
		super();
		this.priority = priority;
		this.templates = templates;
	}

	public MapTemplateLocator(Map<String, String> templates) {
		super();
		this.priority = Priorities.BUILTIN_TEMPLATE_LOCATORS_DEFAULT_PRIORITY;
		this.templates = templates;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public Reader locate(String templateName) {
		String templateContent = templates.get(templateName);
		if (templateContent != null) {
			return new StringReader(templateContent);
		}
		return null;
	}

	@Override
	public Set<String> getAll() {
		return templates.keySet();
	}

	@Override
	public String toString() {
		return String.format("%s [priority: %s]", getClass().getName(),
				getPriority());
	}

}
