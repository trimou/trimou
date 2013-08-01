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
package org.trimou.minify;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder.EngineBuiltCallback;
import org.trimou.lambda.Lambda;

/**
 * This lambda is useful to minify parts of the template contents. Unlike
 * {@link MinifyListener} the minification brings additional overhead - it's
 * performed everytime the template is rendered.
 *
 * Note that if the supplied minifier needs to inspect the configuration to work
 * properly, the lambda instance must be registered as an
 * {@link EngineBuiltCallback}.
 *
 * @author Martin Kouba
 * @see Minifier
 */
public class MinifyLambda implements Lambda, EngineBuiltCallback {

    private InputType inputType = InputType.PROCESSED;

    private boolean isReturnValueInterpolated = false;

    private Minifier minifier;

    /**
     *
     * @param minifier
     */
    public MinifyLambda(Minifier minifier) {
        this.minifier = minifier;
    }

    /**
     *
     * @param inputType
     * @param isReturnValueInterpolated
     * @param minifier
     */
    public MinifyLambda(InputType inputType, boolean isReturnValueInterpolated,
            Minifier minifier) {
        this.inputType = inputType;
        this.isReturnValueInterpolated = isReturnValueInterpolated;
        this.minifier = minifier;
    }

    @Override
    public String invoke(String text) {
        return minifier.minify(text);
    }

    @Override
    public InputType getInputType() {
        return inputType;
    }

    @Override
    public boolean isReturnValueInterpolated() {
        return isReturnValueInterpolated;
    }

    @Override
    public void engineBuilt(MustacheEngine engine) {
        minifier.init(engine.getConfiguration());
    }

}
