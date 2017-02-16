/*
 * Copyright 2017 Trimou Team
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

package org.trimou.engine;

/**
 * Factory to create an instance of {@link MustacheEngine}.
 */
public final class MustacheEngineFactory {

    private MustacheEngineFactory() {
        throw new IllegalAccessError("Factory class");
    }

    /**
     * Creates a new instance of {@link MustacheEngine} with the default settings applied. If you need to
     * configure the engine at one or another point you can either put a properties file {@code trimou-build.properties}
     * in the root directory or initiate an instance by using {@link MustacheEngineBuilder}.
     *
     * @return an instance of {@link MustacheEngine}
     */
    public static MustacheEngine defaultEngine() {
        return MustacheEngineBuilder.newBuilder().build();
    }
}
