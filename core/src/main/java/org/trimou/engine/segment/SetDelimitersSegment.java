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

/**
 * Set the new tag delimiters.
 *
 * <pre>
 * {{=%% %%=}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class SetDelimitersSegment extends AbstractSegment {

	public SetDelimitersSegment(String text, TemplateSegment template) {
		super(text, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.DELIMITERS;
	}

	@Override
	public void execute(Writer writer, ExecutionContext context) {
		// No-op
	}

}
