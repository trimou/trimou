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
package org.trimou.handlebars;

import org.trimou.engine.config.EngineConfigurationKey;

/**
 * Basic built-in helpers are registered automatically if
 * {@link EngineConfigurationKey#HANDLEBARS_SUPPORT_ENABLED} set to
 * <code>true</code>.
 *
 * @author Martin Kouba
 */
public enum BuiltInHelper {

    EACH("each", new EachHelper()),
    IF("if", new IfHelper()),
    UNLESS("unless", new UnlessHelper()),
    WITH("with", new WithHelper()), ;

    private String name;

    private Helper instance;

    private BuiltInHelper(String name, Helper instance) {
        this.name = name;
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public Helper getInstance() {
        return instance;
    }

}
