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
package org.trimou.engine.resolver;

import java.util.UUID;

/**
 * Unlike {@link TransformResolver} this resolver first returns a dummy context
 * object - unique marker - and then performs the transformation.
 *
 * @author Martin Kouba
 */
public class DummyTransformResolver extends TransformResolver {

    private final String marker;

    /**
     *
     * @param priority
     * @param matchNames
     */
    public DummyTransformResolver(int priority,
            String... matchNames) {
        this(priority, null, matchNames);
    }

    /**
     *
     * @param priority
     * @param transformer
     * @param matchNames
     */
    public DummyTransformResolver(int priority,
            Transformer transformer, String... matchNames) {
        super(priority, transformer, matchNames);
        this.marker = UUID.randomUUID().toString();
    }

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null && matches(name)) {
            return marker;
        } else if (contextObject != null && contextObject.equals(marker)) {
            return performTransformation(contextObject, name, context);
        }
        return null;
    }

}
