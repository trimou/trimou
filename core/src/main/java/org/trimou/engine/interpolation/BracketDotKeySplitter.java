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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.trimou.engine.segment.ValueSegment;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * Enables to use bracket notation and literals in {@link ValueSegment} keys.
 * E.g. <code>{{messages["my.message.key"]}}</code>.
 *
 * @author Martin Kouba
 */
public class BracketDotKeySplitter extends DotKeySplitter {

    private static final String PREFIX = "_";

    private final Pattern pattern = Pattern.compile("(\\[\")(.*?)(\"\\])");

    @Override
    public Iterator<String> split(final String key) {

        final int matches = StringUtils.countMatches(key, "[\"");

        if (matches == 0) {
            return super.split(key);
        }

        final Matcher matcher = pattern.matcher(key);
        final StringBuffer buffer = new StringBuffer();
        final Map<String, String> literalMap;

        if (matches == 1) {
            if (matcher.find()) {
                literalMap = Collections.singletonMap(PREFIX, matcher.group(2));
                matcher.appendReplacement(buffer, "." + PREFIX);
            } else {
                literalMap = Collections.emptyMap();
            }
        } else {

            int idx = 0;
            literalMap = new HashMap<String, String>(4);

            while (matcher.find()) {
                String id = PREFIX + idx;
                literalMap.put(id, matcher.group(2));
                matcher.appendReplacement(buffer, "." + id);
                idx++;
            }
        }
        matcher.appendTail(buffer);

        return Iterators.transform(super.split(buffer.toString()),
                new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return literalMap.containsKey(input) ? literalMap
                                .get(input) : input;
                    }
                });
    }

}
