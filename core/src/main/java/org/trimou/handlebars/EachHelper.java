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

import java.lang.reflect.Array;
import java.util.Iterator;

import org.trimou.engine.segment.IterationMeta;

/**
 * TODO
 *
 * @author Martin Kouba
 * @since 1.5.0
 */
public class EachHelper extends AbstractSectionHelper {

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Appendable appendable, Options options) {

        Object value = options.getParameters().get(0);

        if (value instanceof Iterable) {
            processIterable((Iterable) value, appendable, options);
        } else if (value.getClass().isArray()) {
            processArray(value, appendable, options);
        } else {
            // TODO
        }
    }

    @SuppressWarnings("rawtypes")
    private void processIterable(Iterable iterable, Appendable appendable,
            Options options) {

        Iterator iterator = iterable.iterator();

        if (!iterator.hasNext()) {
            return;
        }
        IterationMeta meta = new IterationMeta(iterator);
        options.push(meta);
        while (iterator.hasNext()) {
            processIteration(appendable, options, iterator.next(), meta);
        }
        options.pop();
    }

    private void processArray(Object array, Appendable appendable,
            Options options) {

        int length = Array.getLength(array);

        if (length < 1) {
            return;
        }
        IterationMeta meta = new IterationMeta(length);
        options.push(meta);
        for (int i = 0; i < length; i++) {
            processIteration(appendable, options, Array.get(array, i), meta);
        }
        options.pop();
    }

    private void processIteration(Appendable appendable, Options options,
            Object value, IterationMeta meta) {
        options.push(value);
        options.fn(appendable);
        options.pop();
        meta.nextIteration();
    }

}
