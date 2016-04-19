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
package org.trimou.handlebars;

import static org.trimou.handlebars.HelperValidator.newValidationException;
import static org.trimou.handlebars.Helpers.initIntHashEntry;
import static org.trimou.handlebars.Helpers.isValuePlaceholder;
import static org.trimou.handlebars.OptionsHashKeys.LIMIT;
import static org.trimou.handlebars.OptionsHashKeys.TIMES;
import static org.trimou.handlebars.OptionsHashKeys.WHILE;

import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.HelperDefinition.ValuePlaceholder;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableSet;

/**
 * Allows to repeat the section multiple times:
 *
 * <pre>
 * {{#repeat times=3}}
 *   Hello three times!
 * {{/repeat}}
 * </pre>
 *
 * <p>
 * Or until the {@code while} expression evaluates to a "falsy" value:
 * </p>
 *
 * <pre>
 * {{#with items.iterator}}
 *   {{#repeat while=hasNext}}
 *    {{next}}
 *   {{/repeat}}
 * {{/with}}
 * </pre>
 *
 * <p>
 * An interesting use case might be using {@link ListIterator} to iterate over the list in reverse order:
 * </p>
 *
 * <pre>
 * {{#invoke items.size on=items m="listIterator"}}
 *   {{#repeat while=hasPrevious}}
 *      {{previous}}
 *   {{/repeat}}
 * {{/invoke}}
 * </pre>
 *
 * @author Martin Kouba
 * @see Checker#isFalsy(Object)
 * @since 2.0
 */
public class RepeatHelper extends BasicSectionHelper {

    private final ConcurrentMap<String, String> whilePlaceholderKeys;

    public RepeatHelper() {
        this.whilePlaceholderKeys = new ConcurrentHashMap<>();
    }

    @Override
    public void execute(Options options) {
        Integer times = initIntHashEntry(options, TIMES);
        if (times != null) {
            for (int i = 0; i < times; i++) {
                options.fn();
            }
        } else {
            Object whileValue = options.getHash().get(WHILE);
            if (whileValue != null) {
                Object value = null;
                String whileExpr = whilePlaceholderKeys.get(getKey(options));
                if (whileExpr != null) {
                    value = whileValue;
                } else {
                    whileExpr = whileValue.toString();
                    value = options.getValue(whileExpr);
                }
                int limit = Helpers.initIntHashEntry(options, LIMIT,
                        Integer.MAX_VALUE);
                int i = 0;
                while (!Checker.isFalsy(value)) {
                    options.fn();
                    if (i++ >= limit) {
                        throw new MustacheException(
                                MustacheProblem.RENDER_GENERIC_ERROR,
                                "Iteration limit exceeded");
                    }
                    value = options.getValue(whileExpr);
                }
            }
        }
    }

    @Override
    public void validate(HelperDefinition definition) {
        super.validate(definition);
        if (definition.getHash().containsKey(TIMES)) {
            Object times = definition.getHash().get(TIMES);
            if (!isValuePlaceholder(times) && !(times instanceof Integer)) {
                try {
                    Integer.valueOf(times.toString());
                } catch (NumberFormatException e) {
                    throw new MustacheException(
                            MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                            e);
                }
            }
        } else if (definition.getHash().containsKey(WHILE)) {
            Object whileExpr = definition.getHash().get(WHILE);
            if (isValuePlaceholder(whileExpr)) {
                whilePlaceholderKeys.put(getKey(definition),
                        ((ValuePlaceholder) whileExpr).getName());
            }
        } else {
            throw newValidationException(
                    "Either 'times' or 'while' hash entry is expected",
                    RepeatHelper.class, definition);
        }
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    @Override
    protected int numberOfRequiredHashEntries() {
        return 1;
    }

    @Override
    protected Set<String> getSupportedHashKeys() {
        return ImmutableSet.of(TIMES, WHILE, LIMIT);
    }

    private String getKey(HelperDefinition definition) {
        return definition.getTagInfo().getTemplateGeneratedId()
                + definition.getTagInfo().getId();
    }

}
