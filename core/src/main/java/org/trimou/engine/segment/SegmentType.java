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

/**
 * Type of segment.
 */
public enum SegmentType {

	TEMPLATE,
	VALUE,
	TEXT,
	SECTION,
	INVERTED_SECTION,
	COMMENT,
	LINE_SEPARATOR,
	DELIMITERS,
	PARTIAL,
	// Spec extensions
	EXTEND,
	EXTEND_SECTION
	;

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