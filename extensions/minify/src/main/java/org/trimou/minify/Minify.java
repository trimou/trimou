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

import org.trimou.engine.MustacheEngineBuilder.EngineBuiltCallback;

/**
 * Helper methods to obtain most common minify constructs.
 *
 * @author Martin Kouba
 */
public final class Minify {

    private Minify() {
    }

    /**
     *
     * @return the listener for the default HTML minifier
     */
    public static MinifyListener htmlListener() {
        return new MinifyListener(new HtmlCompressorMinifier());
    }

    /**
     * This lambda needs to inspect the configuration to work properly, the
     * lambda instance must be registered as an {@link EngineBuiltCallback}.
     *
     * @return the lambda for the default HTML minifier
     */
    public static MinifyLambda htmlLambda() {
        return new MinifyLambda(new HtmlCompressorMinifier());
    }

    /**
     *
     * @return the listener for the default XML minifier
     */
    public static MinifyListener xmlListener() {
        return new MinifyListener(new XmlCompressorMinifier());
    }

    /**
     *
     * @return the lambda for the default XML minifier
     */
    public static MinifyLambda xmlLambda() {
        return new MinifyLambda(new XmlCompressorMinifier());
    }

    /**
     *
     * @param minifier
     * @return the listener for a custom minifier
     */
    public static MinifyListener customListener(Minifier minifier) {
        return new MinifyListener(minifier);
    }

    /**
     *
     * @return the lambda for a custom minifier
     */
    public static MinifyLambda customLambda(Minifier minifier) {
        return new MinifyLambda(minifier);
    }

}
