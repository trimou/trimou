package org.trimou.handlebars;


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
public class NumberIsEvenHelper extends NumberMatchingHelper {

    @Override
    protected boolean isMatching(Number value) {
        return value.intValue() % 2 == 0;
    }

}