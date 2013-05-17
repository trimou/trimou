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
package org.trimou.engine.segment;

import org.trimou.engine.MustacheTagType;

/**
 * Type of segment.
 */
public enum SegmentType {

	TEMPLATE(null),
	VALUE(MustacheTagType.VARIABLE),
	TEXT(null),
	SECTION(MustacheTagType.SECTION),
	INVERTED_SECTION(MustacheTagType.INVERTED_SECTION),
	COMMENT(MustacheTagType.COMMENT),
	LINE_SEPARATOR(null),
	DELIMITERS(MustacheTagType.DELIMITER),
	PARTIAL(MustacheTagType.PARTIAL),
	// Spec extensions
	EXTEND(MustacheTagType.EXTEND),
	EXTEND_SECTION(MustacheTagType.EXTEND_SECTION)
	;

	private MustacheTagType tagType;

	SegmentType(MustacheTagType tagType) {
		this.tagType = tagType;
	}

	/**
	 * @return the corresponding tag type or <code>null</code>
	 */
	MustacheTagType getTagType() {
		return tagType;
	}

	public boolean isStandaloneCandidate() {
		return this.equals(COMMENT) || this.equals(SECTION)
				|| this.equals(INVERTED_SECTION) || this.equals(DELIMITERS)
				|| this.equals(PARTIAL);
	}

	public boolean hasName() {
		return this.equals(SECTION) || this.equals(INVERTED_SECTION)
				|| this.equals(PARTIAL) || this.equals(VALUE);
	}

}