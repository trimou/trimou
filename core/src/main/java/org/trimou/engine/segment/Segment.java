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

import java.io.Writer;

import org.trimou.engine.context.ExecutionContext;
import org.trimou.lambda.Lambda.InputType;

/**
 * Compiled template segment. Any segment is considered immutable once the
 * compilation of it's template is finished.
 *
 * @author Martin Kouba
 */
public interface Segment {

	/**
	 * @return the segment type
	 */
	public SegmentType getType();

	/**
	 * @return the segment text (a tag key, a line separator text, etc.)
	 */
	public String getText();

	/**
	 * @return the template this segment belongs to, or <code>null</code> in
	 *         case of this segment itself is a template
	 */
	public TemplateSegment getTemplate();

	/**
	 * @return the reconstructed literal block this segment represents (original
	 *         text before compilation)
	 * @see InputType#LITERAL
	 */
	public String getLiteralBlock();

	/**
	 * Execute this segment (e.g. write down some variable value).
	 *
	 * @param writer
	 * @param context
	 */
	public void execute(Writer writer, ExecutionContext context);

}
