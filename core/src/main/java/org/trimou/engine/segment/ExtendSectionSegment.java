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
import org.trimou.engine.parser.MustacheTag;

/**
 * Either defines a section to be extended/overrided, or an extending/defining section.
 *
 * @author Martin Kouba
 */
public class ExtendSectionSegment extends ContainerSegment {

	public ExtendSectionSegment(String name, TemplateSegment template) {
		super(name, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.EXTEND_SECTION;
	}

	@Override
	public String getLiteralBlock() {
		StringBuilder literal = new StringBuilder();
		literal.append(getTagLiteral(MustacheTag.Type.EXTEND_SECTION.getCommand()
				+ getText()));
		literal.append(super.getLiteralBlock());
		literal.append(getTagLiteral(MustacheTag.Type.SECTION_END.getCommand()
				+ getText()));
		return literal.toString();
	}

	@Override
	public void execute(Writer writer, ExecutionContext context) {

		ExtendSectionSegment defining = context.getDefiningSection(getText());

		if (defining != null) {
			defining.executeNoDefiningLookup(writer, context);
		} else {
			super.execute(writer, context);
		}
	}

	protected void executeNoDefiningLookup(Writer writer, ExecutionContext context) {
		super.execute(writer, context);
	}

}
