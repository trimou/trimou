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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.trimou.engine.context.ExecutionContext;

import com.google.common.collect.ImmutableList;

/**
 * Segment which contains other segments.
 *
 * @author Martin Kouba
 */
public abstract class AbstractContainerSegment extends AbstractSegment implements
		Iterable<Segment> {

	protected List<Segment> segments = new ArrayList<Segment>();

	/**
	 *
	 * @param name
	 * @param template
	 */
	public AbstractContainerSegment(String name, TemplateSegment template) {
		super(name, template);
	}

	public void execute(Appendable appendable, ExecutionContext context) {
		for (Segment segment : segments) {
			segment.execute(appendable, context);
		}
	}

	@Override
	public void performPostProcessing() {
		for (Segment segment : segments) {
			segment.performPostProcessing();
		}
	}

	/**
	 *
	 * @param segment
	 */
	public void addSegment(Segment segment) {
		checkModificationAllowed();
		segments.add(segment);
	}

	@Override
	public Iterator<Segment> iterator() {
		return getSegments().iterator();
	}

	/**
	 * @return the list of contained segments
	 */
	public List<Segment> getSegments() {
		return isReadOnly() ? ImmutableList.copyOf(segments)
				: segments;
	}

	/**
	 * @return the number of contained segments
	 */
	public int getSegmentsSize() {
		return segments.size();
	}

	protected String getContainingLiteralBlock() {
		StringBuilder literal = new StringBuilder();
		for (Segment segment : segments) {
			literal.append(segment.getLiteralBlock());
		}
		return literal.toString();
	}

	@Override
	protected String getSegmentName() {
		return getText();
	}

}
