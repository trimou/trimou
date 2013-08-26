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

import java.util.Map;

/**
 * Abstract resolver for maps with custom key types.
 *
 * @author Martin Kouba
 */
public abstract class MapCustomKeyResolver extends AbstractResolver {

    public MapCustomKeyResolver(int priority) {
        super(priority);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null || !(contextObject instanceof Map)
                || !matches(name)) {
            return null;
        }
        Map map = (Map) contextObject;
        return map.get(convert(name));
    }

    /**
     *
     * @param name
     * @return <code>true</code> if this resolver matches (the lookup should be
     *         performed) the given name, <code>false</code> otherwise
     */
    protected abstract boolean matches(String name);

    /**
     *
     * @param name
     * @return the converted key
     */
    protected abstract Object convert(String name);

}
