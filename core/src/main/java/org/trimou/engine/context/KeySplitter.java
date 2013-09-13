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
package org.trimou.engine.context;

import java.util.Iterator;

/**
 * This interface may become part of the public API so that it's possible to
 * change the way a key is splitted. Not sure about the class name though.
 *
 * @author Martin Kouba
 */
interface KeySplitter {

    /**
     * @param key
     * @return an iterator over the parts of the key
     */
    public Iterator<String> split(String key);

}
