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

import static org.trimou.engine.config.EngineConfigurationKey.END_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.START_DELIMITER;

import java.io.IOException;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.config.Configuration;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

/**
 * Abstract template segment.
 *
 * @author Martin Kouba
 */
public abstract class AbstractSegment implements Segment {

	private final TemplateSegment template;

	private final String text;

	/**
	 *
	 * @param text
	 * @param template
	 */
	public AbstractSegment(String text, TemplateSegment template) {
		super();
		this.text = text;
		this.template = template;
	}

	public String getText() {
		return text;
	}

	public TemplateSegment getTemplate() {
		return template;
	}

	@Override
	public String getLiteralBlock() {
		return getTagLiteral(getText());
	}

	@Override
	public void performPostProcessing() {
		// No-op by default
	}

	@Override
	public String toString() {
		return String
				.format("%s [template: %s, name: %s]", getType(),
						getTemplate() != null ? getTemplate().getText()
								: Strings.EMPTY, getSegmentName());
	}

	protected boolean isReadOnly() {
		return template.isReadOnly();
	}

	protected MustacheEngine getEngine() {
		return template.getEngine();
	}

	protected Configuration getEngineConfiguration() {
		return getEngine().getConfiguration();
	}

	protected String getDefaultStartDelimiter() {
		return getEngineConfiguration().getStringPropertyValue(START_DELIMITER);
	}

	protected String getDefaultEndDelimiter() {
		return getEngineConfiguration().getStringPropertyValue(END_DELIMITER);
	}

	protected String getTagLiteral(String content) {
		return getDefaultStartDelimiter() + content + getDefaultEndDelimiter();
	}

	/**
	 *
	 * @return the segment name
	 */
	protected String getSegmentName() {
		return Strings.EMPTY;
	}

	protected void append(Appendable appendable, String text) {
		try {
			appendable.append(text);
		} catch (IOException e) {
			throw new MustacheException(MustacheProblem.RENDER_IO_ERROR);
		}
	}

	protected void checkModificationAllowed() {
		if (isReadOnly()) {
			throw new MustacheException(
					MustacheProblem.TEMPLATE_MODIFICATION_NOT_ALLOWED,
					toString());
		}
	}

}