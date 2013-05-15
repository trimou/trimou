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
package org.trimou.util;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Martin Kouba
 */
public final class Strings {

	public static final String EMPTY = "";

	public static final String GAP = " ";

	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static final String LINUX_LINE_SEPARATOR = "\n";

	public static final String WINDOWS_LINE_SEPARATOR = "\r\n";

	public static final String SLASH = "/";

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String KEY_SEPARATOR = ".";

	/**
	 *
	 * @param text
	 * @return
	 */
	public static List<String> readLines(String text) {

		List<String> lines = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {

			char val = text.charAt(i);

			if(val == LINUX_LINE_SEPARATOR.charAt(0)) {
				buffer.append(val);
				lines.add(buffer.toString());
				buffer = new StringBuilder();
			} else if((val == WINDOWS_LINE_SEPARATOR.charAt(0)) && (text.charAt(i+1) == WINDOWS_LINE_SEPARATOR.charAt(1))) {
				buffer.append(WINDOWS_LINE_SEPARATOR);
				lines.add(buffer.toString());
				i++;
			} else {
				buffer.append(val);
			}
		}

		if(buffer.length() > 0) {
			lines.add(buffer.toString());
		}
		return lines;
	}

}
