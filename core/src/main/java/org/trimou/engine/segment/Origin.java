package org.trimou.engine.segment;

import org.trimou.annotations.Internal;
import org.trimou.engine.parser.Template;
import org.trimou.util.Strings;

/**
 * A segment origin.
 *
 * @author Martin Kouba
 */
@Internal
public class Origin {

    private final Template template;

    /**
     * The original line where the segment comes from (we cannot calculate this
     * because of "remove standalone lines" spec feature)
     */
    private final Integer line;

    /**
     * An index within the template (segments are parsed sequentially)
     */
    private final Integer index;

    /**
     * An artificial segment.
     *
     * @param template
     */
    public Origin(Template template) {
        super();
        this.template = template;
        this.line = null;
        this.index = null;
    }

    /**
     *
     * @param template
     * @param line
     */
    public Origin(Template template, int line, int index) {
        super();
        this.template = template;
        this.line = line;
        this.index = index;
    }

    public Template getTemplate() {
        return template;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getIndex() {
        return index;
    }

    public String getTemplateName() {
        return template.getName();
    }

    @Override
    public String toString() {
        return String.format("[template: %s, line: %s, idx: %s]", template
                .getName(), line != null ? line : Strings.NOT_AVAILABLE,
                index != null ? index : Strings.NOT_AVAILABLE);
    }

}
