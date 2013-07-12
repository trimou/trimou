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
package org.trimou.engine.priority;

/**
 * A component with priority.
 *
 * @author Martin Kouba
 */
public interface WithPriority {

    public static final int BUILTIN_TEMPLATE_LOCATORS_DEFAULT_PRIORITY = 10;
    public static final int BUILTIN_RESOLVERS_DEFAULT_PRIORITY = 20;
    public static final int EXTENSION_RESOLVERS_DEFAULT_PRIORITY = 10;

    /**
     * @return the priority value
     */
    public int getPriority();

}
