package org.trimou.engine.segment;

/**
 *
 * @author Martin Kouba
 */
final class Lambdas {

    private static final String ONEOFF_LAMBDA_TEMPLATE_PREFIX = "oneoff_lambda";

    private Lambdas() {
    }

    public static String constructLambdaOneoffTemplateName(Segment segment) {
        return new StringBuilder().append(ONEOFF_LAMBDA_TEMPLATE_PREFIX)
                .append("_").append(System.nanoTime()).append(":")
                .append(segment.getOrigin().getTemplateName()).toString();
    }
}
