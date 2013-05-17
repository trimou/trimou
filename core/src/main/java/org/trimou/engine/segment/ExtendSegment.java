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

import org.trimou.engine.context.ExecutionContext;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * This segment extends some template and overrides its extending sections.
 *
 * <pre>
 * {{>super}} {{$insert}}Foo{{/insert}} {{/super}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class ExtendSegment extends AbstractSectionSegment {

	public ExtendSegment(String text, TemplateSegment template) {
		super(text, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.EXTEND;
	}

	@Override
	public void execute(Appendable appendable, ExecutionContext context) {

		TemplateSegment extended = (TemplateSegment) getEngine()
				.getMustache(getText());

		if (extended == null) {
			throw new MustacheException(MustacheProblem.RENDER_INVALID_EXTEND_KEY);
		}

		for (Segment extendSection : this) {
			context.addDefiningSection(extendSection.getText(),
					(ExtendSectionSegment) extendSection);
		}
		extended.execute(appendable, context);
	}

	@Override
	public void addSegment(Segment segment) {
		if (!SegmentType.EXTEND_SECTION.equals(segment.getType())) {
			// Only add extending sections
			return;
		}
		super.addSegment(segment);
	}

}
