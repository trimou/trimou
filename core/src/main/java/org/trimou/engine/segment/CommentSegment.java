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
import org.trimou.engine.context.ExecutionContext;
import org.trimou.util.Strings;

/**
 * Comment segment.
 *
 * <pre>
 * {{! My comment}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class CommentSegment extends AbstractSegment {

	public CommentSegment(String text, TemplateSegment template) {
		super(text, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.COMMENT;
	}

	@Override
	public void execute(Appendable appendable, ExecutionContext context) {
		// No-op
	}

	@Override
	public String getLiteralBlock() {
		return getTagLiteral(MustacheTagType.COMMENT.getCommand() + Strings.GAP
				+ getText());
	}

}
