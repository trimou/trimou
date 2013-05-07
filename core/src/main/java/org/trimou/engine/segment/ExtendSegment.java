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

import org.trimou.MustacheException;
import org.trimou.MustacheProblem;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.engine.parser.MustacheTag;

/**
 * This segment extends some template and overrides its extending sections.
 *
 * <pre>
 * {{>super}} {{$insert}}Foo{{/insert}} {{/super}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class ExtendSegment extends ContainerSegment {

	public ExtendSegment(String name, TemplateSegment template) {
		super(name, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.EXTEND;
	}

	@Override
	public void execute(Writer writer, ExecutionContext context) {

		TemplateSegment extended = (TemplateSegment) getEngine()
				.getMustache(getText());

		if (extended == null) {
			throw new MustacheException(MustacheProblem.RENDER_INVALID_EXTEND_KEY);
		}

		for (Segment extendSection : this) {
			context.addDefiningSection(extendSection.getText(),
					(ExtendSectionSegment) extendSection);
		}
		extended.execute(writer, context);
	}

	@Override
	public void addSegment(Segment segment) {
		if (!SegmentType.EXTEND_SECTION.equals(segment.getType())) {
			// Only add extending sections
			return;
		}
		super.addSegment(segment);
	}

	@Override
	public String getLiteralBlock() {
		StringBuilder literal = new StringBuilder();
		literal.append(getTagLiteral(MustacheTag.Type.EXTEND.getCommand() + getText()));
		literal.append(super.getLiteralBlock());
		literal.append(getTagLiteral(MustacheTag.Type.SECTION_END.getCommand()
				+ getText()));
		return literal.toString();
	}

}
