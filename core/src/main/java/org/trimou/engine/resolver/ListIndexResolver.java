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

import static org.trimou.engine.priority.Priorities.after;

import java.util.List;

/**
 * Resolve index-based access to lists.
 *
 * E.g. get the first element of <code>myList</code>:
 *
 * <pre>
 * {{myList.0}}
 * </pre>
 *
 * This resolver is not registered by default anymore.
 *
 * @author Martin Kouba
 * @deprecated Use {@link CombinedIndexResolver} instead. This class will be removed in the next minor version.
 */
public class ListIndexResolver extends IndexResolver {

    public static final int LIST_RESOLVER_PRIORITY = after(MapResolver.MAP_RESOLVER_PRIORITY, 3);

    public ListIndexResolver() {
        this(LIST_RESOLVER_PRIORITY);
    }

    public ListIndexResolver(int priority) {
        super(priority);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null || notAnIndex(name)
                || !(contextObject instanceof List)) {
            return null;
        }

        List list = (List) contextObject;
        Integer index = getIndexValue(name, list.size());

        if (index != null) {
            return list.get(index);
        }
        return null;
    }

}
