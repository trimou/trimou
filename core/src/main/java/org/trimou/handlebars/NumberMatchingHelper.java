package org.trimou.handlebars;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * An abstract helper for matching {@link Number} values.
 *
 * @author Martin Kouba
 */
public abstract class NumberMatchingHelper extends BasicHelper {

    @Override
    public void execute(Options options) {

        Object param = options.getParameters().get(0);

        if (param instanceof Number) {

            if (isMatching((Number) param)) {
                if (isSection(options)) {
                    options.fn();
                } else {
                    if (options.getParameters().size() > 1) {
                        convertAndAppend(options, options.getParameters().get(1));
                    } else {
                        throw new MustacheException(
                                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                                "Invalid number of params for variable tag [params: %s, template: %s, line: %s]",
                                options.getParameters().size(), options
                                        .getTagInfo().getTemplateName(),
                                options.getTagInfo().getLine());
                    }
                }
            } else {
                if (isVariable(options) && options.getParameters().size() > 2) {
                    convertAndAppend(options, options.getParameters().get(2));
                }
            }
        }
    }

    protected abstract boolean isMatching(Number value);

}