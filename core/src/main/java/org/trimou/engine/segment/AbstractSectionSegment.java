package org.trimou.engine.segment;

import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.EngineConfigurationKey;

/**
 * Abstract section segment is a special type of container. Basically it represents
 * all section-based tags (section, inverted section, extending section, ...).
 *
 * @author Martin Kouba
 */
public abstract class AbstractSectionSegment extends AbstractContainerSegment {

	private String cachedContainingLiteralBlock = null;

	public AbstractSectionSegment(String name, TemplateSegment template) {
		super(name, template);
	}

	@Override
	public String getLiteralBlock() {
		StringBuilder literal = new StringBuilder();
		literal.append(getTagLiteral(getType().getTagType().getCommand()
				+ getText()));
		literal.append(getContainingLiteralBlock());
		literal.append(getTagLiteral(MustacheTagType.SECTION_END.getCommand()
				+ getText()));
		return literal.toString();
	}

	@Override
	protected String getContainingLiteralBlock() {
		if (cachedContainingLiteralBlock != null) {
			return cachedContainingLiteralBlock;
		}
		return super.getContainingLiteralBlock();
	}

	@Override
	public void performPostProcessing() {
		if (getEngineConfiguration().getBooleanPropertyValue(
				EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK)) {
			cachedContainingLiteralBlock = getContainingLiteralBlock();
		}
		super.performPostProcessing();
	}

}
