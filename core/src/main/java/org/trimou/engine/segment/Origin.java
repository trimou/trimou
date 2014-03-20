package org.trimou.engine.segment;

import org.trimou.annotations.Internal;
import org.trimou.engine.parser.Template;

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
     * Artificial segment.
     *
     * @param template
     */
    public Origin(Template template) {
        super();
        this.template = template;
        this.line = null;
    }

    /**
     *
     * @param template
     * @param line
     */
    public Origin(Template template, int line) {
        super();
        this.template = template;
        this.line = line;
    }

    public Template getTemplate() {
        return template;
    }

    public int getLine() {
        return line;
    }

    public String getTemplateName() {
        return template.getName();
    }

    @Override
    public String toString() {
        return String.format("[template: %s, line: %s]", template.getName(),
                line != null ? line : "N/A");
    }

}
