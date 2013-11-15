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
package org.trimou.engine.interpolation;

import java.util.Iterator;

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.segment.ValueSegment;

/**
 * This component is responsible for splitting a variable key.
 *
 * @author Martin Kouba
 * @see ValueSegment
 */
public interface KeySplitter extends ConfigurationAware {

    /**
     * @param key
     * @return an iterator over the parts of the key
     */
    public Iterator<String> split(String key);

}
