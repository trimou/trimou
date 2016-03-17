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

import static org.trimou.handlebars.OptionsHashKeys.DELIMITER;
import static org.trimou.handlebars.OptionsHashKeys.LAMBDA;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.lambda.Lambda;
import org.trimou.lambda.Lambda.InputType;
import org.trimou.util.ImmutableSet;


/**
 * This helper takes all the objects specified as the parameters and joins the
 * {@link Object#toString()} values together with the specified delimiter
 * (optional). Elements of {@link Iterable}s and arrays are treated as separate
 * objects.
 *
 * <p>
 * The following template will render "Hello Martin, John!" for the list of
 * strings "Martin" and "John":
 * </p>
 *
 * <pre>
 * Hello {{join listOfNames delimiter=", " }}!
 * </pre>
 *
 * <p>
 * Note that the output is escaped (if needed). It's possible to use unescape
 * variable though, e.g.:
 * </p>
 *
 * <pre>
 * Hello {{&join "<strong>Martin</strong>" "John" delimiter=", " }}
 * </pre>
 *
 * <p>
 * An optional lambda may be applied to each value. Note that the
 * lambda is always processed as with {@link InputType#LITERAL} and
 * {@link Lambda#isReturnValueInterpolated()} set to false.
 * </p>
 *
 * <pre>
 * Hello {{&join "Martin" "John" delimiter=" " lambda=makeItalic}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class JoinHelper extends BasicValueHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(JoinHelper.class);

    @Override
    public void execute(Options options) {

        final Object delimiter = getHashValue(options, DELIMITER);
        final Lambda lambda = initLambda(options);

        if (options.getParameters().size() == 1) {
            processValue(options, options.getParameters().get(0), delimiter,
                    lambda);
        } else {
            for (Iterator<Object> iterator = options.getParameters().iterator(); iterator
                    .hasNext();) {
                Object value = iterator.next();
                processValue(options, value, delimiter, lambda);
                if (iterator.hasNext() && delimiter != null) {
                    append(options, delimiter, null);
                }
            }
        }
    }


    @Override
    protected Optional<Set<String>> getSupportedHashKeys() {
        return Optional.of(ImmutableSet.of(DELIMITER, LAMBDA));
    }

    private void processValue(Options options, Object value, Object delimiter,
            Lambda lambda) {
        if (value == null) {
            return;
        } else if (value instanceof Iterable) {
            processIterable(options, (Iterable<?>) value, delimiter, lambda);
        } else if (value.getClass().isArray()) {
            processArray(options, value, delimiter, lambda);
        } else {
            append(options, value, lambda);
        }
    }

    @SuppressWarnings("rawtypes")
    private void processIterable(Options options, Iterable iterable,
            Object delimiter, Lambda lambda) {
        Iterator iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        while (iterator.hasNext()) {
            append(options, iterator.next(), lambda);
            if (delimiter != null && iterator.hasNext()) {
                append(options, delimiter, null);
            }
        }
    }

    private void processArray(Options options, Object array, Object delimiter,
            Lambda lambda) {
        int length = Array.getLength(array);
        if (length < 1) {
            return;
        }
        for (int i = 0; i < length; i++) {
            append(options, Array.get(array, i), lambda);
            if (delimiter != null && (i + 1 < length)) {
                append(options, delimiter, null);
            }
        }
    }

    private void append(Options options, Object value, Lambda lambda) {
        append(options, lambda != null ? lambda.invoke(value.toString())
                : value.toString());
    }

    private Lambda initLambda(Options options) {
        final Object lambdaReference = getHashValue(options, LAMBDA);
        if (lambdaReference == null) {
            return null;
        }
        if (lambdaReference instanceof Lambda) {
            Lambda lambda = (Lambda) lambdaReference;
            if (lambda.isReturnValueInterpolated()
                    || lambda.getInputType().equals(InputType.PROCESSED)) {
                logger.warn(
                        "The lambda is processed as with InputType#LITERAL and Lambda#isReturnValueInterpolated() set to false [{}]",
                        options.getTagInfo());
            }
            return lambda;
        } else {
            throw new MustacheException(
                    MustacheProblem.RENDER_HELPER_INVALID_OPTIONS,
                    "%s is not a valid Lambda reference [%s]", lambdaReference,
                    options.getTagInfo());
        }
    }

}
