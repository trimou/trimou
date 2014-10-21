/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.engine.segment;

import org.trimou.engine.parser.Template;
import org.trimou.lambda.Lambda;

/**
 *
 * @author Martin Kouba
 */
final class Lambdas {

    private Lambdas() {
    }

    /**
     *
     * @param segment
     * @return the name for one-off lambda, e.g. "oneoff_lambda_10_4242"
     * @see Lambda#isReturnValueInterpolated()
     */
    public static String constructLambdaOneoffTemplateName(Segment segment) {
        final Template template = segment.getOrigin().getTemplate();
        return new StringBuilder()
                .append(Lambda.ONEOFF_LAMBDA_TEMPLATE_PREFIX)
                .append(template.getGeneratedId())
                .append("_")
                .append(template.getEngine().getConfiguration()
                        .getIdentifierGenerator().generate())
                .toString();
    }
}
