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

import static org.trimou.engine.priority.Priorities.rightAfter;

import java.util.Map;

/**
 * Resolves {@link Map} values. The key is expected to be an instance of a
 * {@link String}.
 *
 * @author Martin Kouba
 */
public class MapResolver extends AbstractResolver {

    public static final int MAP_RESOLVER_PRIORITY = rightAfter(ThisResolver.THIS_RESOLVER_PRIORITY);

    public MapResolver() {
        this(MAP_RESOLVER_PRIORITY);
    }

    public MapResolver(int priority) {
        super(priority);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null || !(contextObject instanceof Map)) {
            return null;
        }

        Map map = (Map) contextObject;
        return map.get(name);
    }

}
