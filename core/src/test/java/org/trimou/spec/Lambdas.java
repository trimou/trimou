package org.trimou.spec;

import java.util.HashMap;
import java.util.Map;

import org.trimou.lambda.Lambda;
import org.trimou.lambda.SpecCompliantLambda;

public final class Lambdas {

    /**
     * Template name -> lambda
     */
    public static final Map<String, Lambda> testMap = init();

    private static Map<String, Lambda> init() {

        Map<String, Lambda> lambdas = new HashMap<>();

        lambdas.put("Interpolation", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return "world";
            }
        });

        lambdas.put("Interpolation - Expansion", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return "{{planet}}";
            }
        });

        lambdas.put("Interpolation - Multiple Calls",
                new SpecCompliantLambda() {

                    private int calls = 0;

                    @Override
                    public String invoke(String text) {
                        calls++;
                        return "" + calls;
                    }
                });

        lambdas.put("Escaping", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return ">";
            }

        });

        lambdas.put("Interpolation - Alternate Delimiters",
                new SpecCompliantLambda() {

                    @Override
                    public String invoke(String text) {
                        return "|planet| => {{planet}}";
                    }
                });

        lambdas.put("Section", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return text.equals("{{x}}") ? "yes" : "no";
            }
        });

        lambdas.put("Section - Expansion", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return text + "{{planet}}" + text;
            }
        });

        lambdas.put("Section - Alternate Delimiters",
                new SpecCompliantLambda() {

                    @Override
                    public String invoke(String text) {
                        return text + "{{planet}} => |planet|" + text;
                    }
                });

        lambdas.put("Section - Multiple Calls", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return "__" + text + "__";
            }
        });

        lambdas.put("Inverted Section", new SpecCompliantLambda() {

            @Override
            public String invoke(String text) {
                return "false";
            }
        });

        return lambdas;
    }

}
