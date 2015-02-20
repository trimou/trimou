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

import static org.trimou.handlebars.OptionsHashKeys.FILTER;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.segment.IterationMeta;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Filters.Filter;

/**
 * <code>
 * {{#each items}}
 *  {{name}}
 * {{/each}}
 * </code>
 *
 * <p>
 * It's possible to filter out unnecessary elements:
 * </p>
 * <code>
 * {{#each items filter=mySuperFilter}}
 *  {{name}}
 * {{/each}}
 * </code>
 * <p>
 * The filter must be an instance of {@link Filter}. Note that the filter cannot
 * be type-safe.
 * </p>
 *
 * @see Filter
 * @author Martin Kouba
 */
public class EachHelper extends BasicSectionHelper {

    private String iterationMetadataAlias;

    @Override
    public void init() {
        super.init();
        this.iterationMetadataAlias = configuration
                .getStringPropertyValue(EngineConfigurationKey.ITERATION_METADATA_ALIAS);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Options options) {

        Object value = options.getParameters().get(0);
        Filter filter = initFilter(options);

        if (value instanceof Iterable) {
            processIterable((Iterable) value, filter, options);
        } else if (value.getClass().isArray()) {
            processArray(value, filter, options);
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "%s is nor an Iterable nor an array [%s]", value,
                    options.getTagInfo());
        }
    }

    @SuppressWarnings("rawtypes")
    private void processIterable(Iterable iterable, Filter filter,
            Options options) {

        Iterator iterator = iterable.iterator();

        if (!iterator.hasNext()) {
            return;
        }
        IterationMeta meta = new IterationMeta(iterationMetadataAlias, iterator);
        options.push(meta);
        while (iterator.hasNext()) {
            processIteration(options, iterator.next(), meta, filter);
        }
        options.pop();
    }

    private void processArray(Object array, Filter filter, Options options) {

        int length = Array.getLength(array);

        if (length < 1) {
            return;
        }
        IterationMeta meta = new IterationMeta(iterationMetadataAlias, length);
        options.push(meta);
        for (int i = 0; i < length; i++) {
            processIteration(options, Array.get(array, i), meta, filter);
        }
        options.pop();
    }

    private void processIteration(Options options, Object value,
            IterationMeta meta, Filter filter) {
        if (filter == null || filter.test(value)) {
            options.push(value);
            options.fn();
            options.pop();
            meta.nextIteration();
        }
    }

    private Filter initFilter(Options options) {
        Object filter = getHashValue(options, FILTER);
        if (filter == null) {
            return null;
        }
        if (filter instanceof Filter) {
            return (Filter) filter;
        }
        throw new MustacheException(
                MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                "%s is not a valid filter [%s]", filter, options.getTagInfo());
    }

}
