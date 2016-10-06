/*
 * Copyright 2016 Martin Kouba
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
package org.trimou.engine.convert;

/**
 *
 * @author Martin Kouba
 */
public abstract class AbstractValueConverter implements ValueConverter {

    public static final int DEFAULT_PRIORITY = 10;

    protected volatile boolean isEnabled;

    private final int priority;

    public AbstractValueConverter() {
        this(DEFAULT_PRIORITY);
    }

    /**
     *
     * @param priority
     */
    public AbstractValueConverter(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isValid() {
        return isEnabled;
    }

}
