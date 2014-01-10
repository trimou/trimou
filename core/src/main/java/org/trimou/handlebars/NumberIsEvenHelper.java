package org.trimou.handlebars;

import org.trimou.engine.MustacheTagType;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * <code>
 * {{isEven iterIndex "evenRow"}}
 * </code>
 *
 * <code>
 * {{isEven iterIndex "evenRow" "oddRow"}}
 * </code>
 *
 * <code>
 * {{#isEven iterIndex}}
 * ...
 * {{/isEven}}
 * </code>
 *
 * @author Martin Kouba
 */
public class NumberIsEvenHelper extends AbstractHelper {

    @Override
    public void validate(HelperDefinition definition) {
        HelperValidator.checkType(this.getClass(), definition,
                MustacheTagType.VARIABLE, MustacheTagType.UNESCAPE_VARIABLE,
                MustacheTagType.SECTION);
        HelperValidator.checkParams(this.getClass(), definition, 1);
    }

    @Override
    public void execute(Options options) {

        Object param = options.getParameters().get(0);

        if (param instanceof Number) {

            if (isEven((Number) param)) {
                if (isSection(options)) {
                    options.fn();
                } else {
                    if (options.getParameters().size() > 1) {
                        options.append(options.getParameters().get(1)
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
                    options.append(options.getParameters().get(2).toString());
                }
            }
        }
    }

    private boolean isEven(Number value) {
        return value.intValue() % 2 == 0;
    }

}