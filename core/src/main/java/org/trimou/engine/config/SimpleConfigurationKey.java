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
package org.trimou.engine.config;

/**
 *
 * @author Martin Kouba
 */
public class SimpleConfigurationKey implements ConfigurationKey {

	private String key;

	private Object defaultValue;

	/**
	 *
	 * @param key
	 * @param defaultValue
	 */
	public SimpleConfigurationKey(String key, Object defaultValue) {
		super();
		this.key = key;
		this.defaultValue = defaultValue;
	}

	@Override
	public String get() {
		return key;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

}
