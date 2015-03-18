/*
 * Copyright 2014 Martin Kouba
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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.validation.Validateable;

/**
 * A combined resolver which is able to resolve index-based access to lists and
 * arrays.
 *
 * @author Martin Kouba
 * @see IndexResolver
 * @see ListIndexResolver
 * @see ArrayIndexResolver
 */
public class CombinedIndexResolver extends IndexResolver implements Validateable {

    private boolean isEnabled;

    /**
     * If set to <code>false</code> the resolver is marked as invalid.
     */
    public static final ConfigurationKey ENABLED_KEY = new SimpleConfigurationKey(
            CombinedIndexResolver.class.getName() + ".enabled", true);

    /**
     *
     */
    public CombinedIndexResolver() {
        this(ListIndexResolver.LIST_RESOLVER_PRIORITY);
    }

    /**
     *
     * @param priority
     */
    public CombinedIndexResolver(int priority) {
        super(priority);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject == null || notAnIndex(name)) {
            return null;
        }

        if (contextObject instanceof List) {
            List list = (List) contextObject;
            Integer index = getIndexValue(name, list.size());
            if (index != null) {
                return list.get(index);
            }
        }

        if (contextObject.getClass().isArray()) {
            Integer index = getIndexValue(name, Array.getLength(contextObject));
            if (index != null) {
                return Array.get(contextObject, index);
            }
        }
        return null;
    }



    @Override
    public void init(Configuration configuration) {
        isEnabled = configuration.getBooleanPropertyValue(ENABLED_KEY);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ENABLED_KEY);
    }

    @Override
    public boolean isValid() {
        return isEnabled;
    }

}
