package org.trimou.example.ping;

import org.trimou.lambda.InputProcessingLambda;

/**
 *
 * @author Martin Kouba
 */
public class OddEvenIndexLambda extends InputProcessingLambda {

    private static final String EVEN_CSS_CLASS = "even";
    private static final String ODD_CSS_CLASS = "odd";

    @Override
    public String invoke(String indexStr) {

        final Integer index;

        try {
            index = Integer.valueOf(indexStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("%s is not an integer index", indexStr));
        }

        if (index % 2 == 0) {
            return EVEN_CSS_CLASS;
        }
        return ODD_CSS_CLASS;
    }

    @Override
    public boolean isReturnValueInterpolated() {
        return false;
    }

}
