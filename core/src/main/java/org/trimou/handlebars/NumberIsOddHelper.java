package org.trimou.handlebars;


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
public class NumberIsOddHelper extends NumberMatchingHelper {

    @Override
    protected boolean isMatching(Number value) {
        return value.intValue() % 2 != 0;
    }

}