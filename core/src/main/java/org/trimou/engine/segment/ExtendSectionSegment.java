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

/**
 * Either defines a section to be extended/overrided, or an extending/defining section.
 *
 * @author Martin Kouba
 */
public class ExtendSectionSegment extends AbstractSectionSegment {

	public ExtendSectionSegment(String text, TemplateSegment template) {
		super(text, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.EXTEND_SECTION;
	}

	@Override
	public void execute(Appendable appendable, ExecutionContext context) {

		ExtendSectionSegment defining = context.getDefiningSection(getText());

		if (defining != null) {
			defining.executeNoDefiningLookup(appendable, context);
		} else {
			super.execute(appendable, context);
		}
	}

	protected void executeNoDefiningLookup(Appendable appendable, ExecutionContext context) {
		super.execute(appendable, context);
	}

}
