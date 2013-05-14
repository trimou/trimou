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
import java.util.List;

import org.trimou.engine.MustacheTagType;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Partial segment.
 *
 * @author Martin Kouba
 */
public class PartialSegment extends AbstractSegment {

	private String indentation;

	public PartialSegment(String name, TemplateSegment template) {
		super(name, template);
	}

	@Override
	public SegmentType getType() {
		return SegmentType.PARTIAL;
	}

	@Override
	public void execute(Writer writer, ExecutionContext context) {

		TemplateSegment partialTemplate = (TemplateSegment) getEngine().getMustache(
				getText());

		if (partialTemplate == null) {
			throw new MustacheException(
					MustacheProblem.RENDER_INVALID_PARTIAL_KEY);
		}

		if (indentation == null) {
			partialTemplate.execute(writer, context);
		} else {
			// Prepend indentation before rendering
			List<List<Segment>> partialLines = Segments
					.readSegmentLinesBeforeRendering(partialTemplate);
			TextSegment indent = new TextSegment(indentation, getTemplate());

			for (List<Segment> line : partialLines) {
				line.add(0, indent);
			}

			for (List<Segment> line : partialLines) {
				for (Segment segment : line) {
					segment.execute(writer, context);
				}
			}
		}
	}

	@Override
	public String getLiteralBlock() {
		return getTagLiteral(MustacheTagType.PARTIAL.getCommand() + getText());
	}

	public void setIndentation(String indentation) {
		checkModificationAllowed();
		this.indentation = indentation;
	}

}
