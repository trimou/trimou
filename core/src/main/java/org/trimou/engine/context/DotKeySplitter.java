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

import org.trimou.util.Strings;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;

/**
 * Default {@link KeySplitter} implementation.
 *
 * @author Martin Kouba
 */
class DotKeySplitter implements KeySplitter {

    private final Splitter splitter = Splitter.on(Strings.DOT)
            .omitEmptyStrings();

    @Override
    public Iterator<String> split(final String key) {
        if (key.equals(Strings.DOT) || !key.contains(Strings.DOT)) {
                    return Iterators.singletonIterator(key);
        }
        return splitter.split(key).iterator();
    }

}
