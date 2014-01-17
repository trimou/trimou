package org.trimou.handlebars;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * <code>
 * {{isOdd iterIndex "oddRow"}}
 * </code>
 *
 * <code>
 * {{isOdd iterIndex "oddRow" "evenRow"}}
 * </code>
 *
 * <code>
 * {{#isOdd iterIndex}}
 * ...
 * {{/isEven}}
 * </code>
 *
 * @author Martin Kouba
 */
public class NumberIsOddHelper extends BasicHelper {

    @Override
    public void execute(Options options) {

        Object param = options.getParameters().get(0);

        if (param instanceof Number) {

            if (isOdd((Number) param)) {
                if (isSection(options)) {
                    options.fn();
                } else {
                    if (options.getParameters().size() > 1) {
                        append(options, options.getParameters().get(1)
                                .toString());
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
                    append(options, options.getParameters().get(2).toString());
                }
            }
        }
    }

    private boolean isOdd(Number value) {
        return value.intValue() % 2 != 0;
    }

}