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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.engine.segment.ValueSegment;
import org.trimou.util.Strings;

import com.google.common.collect.Iterators;

/**
 * Enables to use bracket notation and literals in {@link ValueSegment} keys.
 * E.g. <code>{{messages["my.message.key"]}}</code>.
 *
 * @author Martin Kouba
 */
public class BracketDotKeySplitter extends AbstractConfigurationAware implements
        KeySplitter {

    @Override
    public Iterator<String> split(final String key) {

        if (key.equals(Strings.DOT)) {
            return Iterators.singletonIterator(Strings.DOT);
        }
        if (key.equals(Strings.THIS)) {
            return Iterators.singletonIterator(Strings.THIS);
        }

        boolean stringLiteral = false;
        boolean separator = false;
        List<String> parts = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < key.length(); i++) {
            if (isSeparator(key.charAt(i))) {
                // Only process the first separator - adjacent separators are
                // ignored
                if (!separator) {
                    if (!stringLiteral) {
                        if (buffer.length() > 0) {
                            parts.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        separator = true;
                    } else {
                        buffer.append(key.charAt(i));
                    }
                }
            } else {
                // Non-separator char
                if (Strings.isStringLiteralSeparator(key.charAt(i))) {
                    stringLiteral = !stringLiteral;
                } else {
                    buffer.append(key.charAt(i));
                }
                separator = false;
            }
        }

        if (buffer.length() > 0) {
            parts.add(buffer.toString());
        }
        return parts.iterator();
    }

    private boolean isSeparator(char candidate) {
        return candidate == '.' || candidate == '[' || candidate == ']';
    }

}
