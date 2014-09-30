package org.trimou.engine.segment;

import java.util.List;

import org.trimou.engine.MustacheTagType;
import org.trimou.engine.config.EngineConfigurationKey;

/**
 * Abstract section segment is a special type of container. Basically it
 * represents all section-based tags (section, inverted section, extending
 * section, ...).
 *
 * @author Martin Kouba
 */
abstract class AbstractSectionSegment extends AbstractContainerSegment {

    private final String cachedContentLiteralBlock;

    public AbstractSectionSegment(String name, Origin origin,
            List<Segment> segments) {
        super(name, origin, segments);
        if (getEngineConfiguration().getBooleanPropertyValue(
                EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK)) {
            this.cachedContentLiteralBlock = getContentLiteralBlock();
        } else {
            this.cachedContentLiteralBlock = null;
        }
    }

    @Override
    public String getLiteralBlock() {
        StringBuilder literal = new StringBuilder();
        literal.append(getTagLiteral(getType().getTagType().getCommand()
                + getText()));
        literal.append(getContentLiteralBlock());
        literal.append(getTagLiteral(MustacheTagType.SECTION_END.getCommand()
                + getText()));
        return literal.toString();
    }

    @Override
    public String getContentLiteralBlock() {
        if (cachedContentLiteralBlock != null) {
            return cachedContentLiteralBlock;
        }
        return super.getContentLiteralBlock();
    }

}
