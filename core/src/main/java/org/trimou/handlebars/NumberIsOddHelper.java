package org.trimou.handlebars;

import org.trimou.engine.MustacheTagType;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * <code>
 * {{isOdd iterIndex "oddRow"}}
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
public class NumberIsOddHelper extends AbstractHelper {

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

        if (param instanceof Number && (((Number) param).intValue() % 2 != 0)) {

            if (options.getTagInfo().getType().equals(MustacheTagType.SECTION)) {
                options.fn();
            } else {
                if (options.getParameters().size() > 1) {
                    options.append(options.getParameters().get(1).toString());
                } else {
                    throw new MustacheException(
                            MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                            "Invalid number of params for variable tag [params: %s, template: %s, line: %s]",
                            options.getParameters().size(), options
                                    .getTagInfo().getTemplateName(), options
                                    .getTagInfo().getLine());
                }
            }
        }
    }

}