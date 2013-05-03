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

/**
 *
 * @author Martin Kouba
 */
public enum MustacheProblem {

	// Compilation problems
	COMPILE_INVALID_DELIMITERS,
	COMPILE_INVALID_TAG,
	COMPILE_INVALID_SECTION_END,
	COMPILE_INVALID_TEMPLATE,
	// Template related problems
	TEMPLATE_NOT_READY,
	TEMPLATE_MODIFICATION_NOT_ALLOWED,
	TEMPLATE_LOCATOR_INVALID_CONFIGURATION,
	TEMPLATE_LOADING_ERROR,
	// Rendering problems
	RENDER_INVALID_PARTIAL_KEY,
	RENDER_INVALID_EXTEND_KEY,
	RENDER_IO_ERROR,
	RENDER_REFLECT_INVOCATION_ERROR,
	RENDER_NO_VALUE,

}
