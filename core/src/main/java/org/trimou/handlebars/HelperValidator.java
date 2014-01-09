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

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheTagType;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;

import com.google.common.base.Preconditions;

/**
 *
 * @author Martin Kouba
 * @since 1.5
 */
public final class HelperValidator {

    private static final Logger logger = LoggerFactory
            .getLogger(HelperValidator.class);

    private HelperValidator() {
    }

    /**
     *
     * @param helperClazz
     * @param definition
     * @param paramSize
     * @throws MustacheException
     *             If the helper tag params
     */
    public static void checkParams(Class<?> helperClazz,
            HelperDefinition definition, int paramSize) {
        Checker.checkArgumentNotNull(definition);
        Preconditions.checkArgument(paramSize >= 0,
                "Helper may only require zero or more params");

        int size = definition.getParameters().size();

        if (size < paramSize) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                    "Insufficient number of parameters for helper %s [expected: %s, current: %s, template: %s, line: %s]",
                    helperClazz.getName(), paramSize, size, definition
                            .getTagInfo().getTemplateName(), definition
                            .getTagInfo().getLine());
        }

        if (size > paramSize) {
            logger.warn(
                    "{} unused parameters detected [helper: %s, template: %s, line: %s]",
                    size - paramSize, helperClazz.getName(), definition
                            .getTagInfo().getTemplateName(), definition
                            .getTagInfo().getLine());
        }
    }

    /**
     *
     * @param helperClazz
     * @param definition
     * @param allowedTypes
     * @throws MustacheException
     *             If the helper tag type does not match any one of the
     *             specified types
     */
    public static void checkType(Class<?> helperClazz,
            HelperDefinition definition, MustacheTagType... allowedTypes) {
        Checker.checkArgumentsNotNull(definition, allowedTypes);
        if (!ArrayUtils.contains(allowedTypes, definition.getTagInfo()
                .getType())) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                    "Unsupported tag type [helper: %s, template: %s, line: %s]",
                    helperClazz.getName(), definition.getTagInfo()
                            .getTemplateName(), definition.getTagInfo()
                            .getLine());
        }
    }

}
