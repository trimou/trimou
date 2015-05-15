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

import org.trimou.engine.config.AbstractConfigurationAware;

/**
 * Abstract resolver.
 *
 * @author Martin Kouba
 */
public abstract class AbstractResolver extends AbstractConfigurationAware
        implements EnhancedResolver {

    private final int priority;

    public AbstractResolver(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Hint createHint(Object contextObject, String name,
            ResolutionContext context) {
        return EnhancedResolver.INAPPLICABLE_HINT;
    }

    @Override
    public String toString() {
        return String.format("%s [priority: %s]", getClass().getName(),
                getPriority());
    }

}
