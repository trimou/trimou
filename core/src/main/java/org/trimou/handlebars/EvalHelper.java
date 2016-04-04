/*
 * Copyright 2015 Martin Kouba
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

import java.util.Iterator;

import org.trimou.engine.interpolation.BracketDotKeySplitter;
import org.trimou.engine.interpolation.DotKeySplitter;
import org.trimou.engine.interpolation.KeySplitter;
import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.util.Strings;

/**
 * Allows to build the key dynamically and evaluate it afterwards.
 *
 * <p>
 * First a key is built from the params, or rather their
 * {@link Object#toString()} representations, by default dot notation is used.
 * Then the helper attempts to find the value from the context. If the helper
 * represents a section and the value is not null the value is pushed on the
 * context stack and the section is rendered. If the helper represents a
 * variable and the value is null, the current {@link MissingValueHandler} is
 * used. If the helper represents a variable and the final value is not null the
 * the value's {@link Object#toString()} is rendered.
 * </p>
 *
 * <pre>
 * {{eval "foo" "bar"}}
 * </pre>
 *
 * is equivalent to:
 *
 * <pre>
 * {{foo.bar}}
 * </pre>
 *
 * On the other hand:
 *
 * <pre>
 * {{#eval "list" idx}}
 *  Element name: {{name}}
 * {{/eval}}
 * </pre>
 *
 * is equivalent to (provided the idx evaluates to 1):
 *
 * <pre>
 * {{#with list.1}}
 *  Element name: {{name}}
 * {{/with}}
 * </pre>
 *
 * @author Martin Kouba
 * @see Notation
 * @see KeySplitter
 */
public class EvalHelper extends BasicHelper {

    private final Notation notation;

    /**
     *
     * @see DotNotation
     */
    public EvalHelper() {
        this(new DotNotation());
    }

    /**
     *
     * @param notation The notation to be used when constructing keys
     */
    public EvalHelper(Notation notation) {
        this.notation = notation;
    }

    @Override
    public void execute(Options options) {
        StringBuilder key = new StringBuilder();
        Iterator<Object> iterator = options.getParameters().iterator();
        while (iterator.hasNext()) {
            notation.append(key, iterator.next().toString());
        }
        Object value = options.getValue(key.toString());
        if (isSection(options)) {
            if (value != null) {
                options.push(value);
                options.fn();
                options.pop();
            }
        } else {
            if (value == null) {
                value = configuration.getMissingValueHandler().handle(
                        options.getTagInfo());
            }
            if (value != null) {
                append(options, value.toString());
            }
        }
    }

    public interface Notation {

        /**
         * Append the given part to the key builder.
         *
         * @param builder
         * @param part
         */
        void append(StringBuilder builder, String part);

    }

    /**
     *
     * @author Martin Kouba
     * @see DotKeySplitter
     */
    public static class DotNotation implements Notation {

        @Override
        public void append(StringBuilder builder, String part) {
            if (builder.length() > 0) {
                builder.append(Strings.DOT);
            }
            builder.append(part);
        }

    }

    /**
     *
     * @author Martin Kouba
     * @see BracketDotKeySplitter
     */
    public static class BracketDotNotation implements Notation {

        @Override
        public void append(StringBuilder builder, String part) {
            if (builder.length() > 0) {
                builder.append("[\"");
                builder.append(part);
                builder.append("\"]");
            } else {
                builder.append(part);
            }
        }

    }

}
