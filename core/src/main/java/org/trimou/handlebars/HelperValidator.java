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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheTagType;
import org.trimou.engine.segment.Segment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;
import org.trimou.util.Strings;

import com.google.common.base.Optional;
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
     *             If the helper expects more params
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
            logger.trace(
                    "{} superfluous parameters detected [helper: {}, template: {}, line: {}]",
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

    /**
     *
     * @param helperClazz
     * @param definition
     * @param hashSize
     * @throws MustacheException
     *             If the helper expects more hash entries
     */
    public static void checkHash(Class<?> helperClazz,
            HelperDefinition definition, int hashSize) {
        Checker.checkArgumentNotNull(definition);
        Preconditions.checkArgument(hashSize >= 0,
                "Helper may only require zero or more hash entries");

        int size = definition.getHash().size();

        if (size < hashSize) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                    "Insufficient number of hash entries for helper %s [expected: %s, current: %s, template: %s, line: %s]",
                    helperClazz.getName(), hashSize, size, definition
                            .getTagInfo().getTemplateName(), definition
                            .getTagInfo().getLine());
        }

        if (size > hashSize) {
            logger.trace(
                    "{} superfluous hash entries detected [helper: {}, template: {}, line: {}]",
                    size - hashSize, helperClazz.getName(), definition
                            .getTagInfo().getTemplateName(), definition
                            .getTagInfo().getLine());
        }
    }

    /**
     *
     * @param definition
     * @param hashSize
     * @see #checkHash(Class, HelperDefinition, int)
     */
    public static void checkHash(HelperDefinition definition, BasicHelper helper) {
        // Number of required hash entries
        checkHash(helper.getClass(), definition,
                helper.numberOfRequiredHashEntries());
        // Log a warning message if an unsupported hash key is found
        Optional<Set<String>> supportedHashKeys = helper.getSupportedHashKeys();
        if (supportedHashKeys.isPresent()) {
            for (String key : definition.getHash().keySet()) {
                if (!supportedHashKeys.get().contains(key)) {
                    logger.info(
                            "Unsupported hash key detected [key: {}, helper: {}, template: {}, line: {}]",
                            key, helper.getClass().getName(), definition
                                    .getTagInfo().getTemplateName(), definition
                                    .getTagInfo().getLine());
                }
            }
        }
    }

    /**
     * Extracts parts from an input string. This implementation is quite naive
     * and should be possibly rewritten. Note that we can't use a simple
     * splitter because of string literals may contain whitespace chars.
     *
     * @param name
     * @param segment
     * @return the parts of the helper name
     * @throws MustacheException
     *             If a compilation problem occures
     */
    @Internal
    public static Iterator<String> splitHelperName(String name, Segment segment) {

        boolean stringLiteral = false;
        boolean space = false;
        List<String> parts = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ') {
                if (!space) {
                    if (!stringLiteral) {
                        if (buffer.length() > 0) {
                            parts.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        space = true;
                    } else {
                        buffer.append(name.charAt(i));
                    }
                }
            } else {
                if (Strings.isStringLiteralSeparator(name.charAt(i))) {
                    stringLiteral = !stringLiteral;
                }
                space = false;
                buffer.append(name.charAt(i));
            }
        }

        if (buffer.length() > 0) {
            if (stringLiteral) {
                throw new MustacheException(
                        MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                        "Unterminated string literal detected: %s", segment);
            }
            parts.add(buffer.toString());
        }
        return parts.iterator();
    }

    /**
     *
     * @param part
     * @return the index of an equals char outside of any string literal,
     *         <code>-1</code> if no such char is found
     */
    public static int getFirstDeterminingEqualsCharPosition(String part) {
        boolean stringLiteral = false;
        for (int i = 0; i < part.length(); i++) {
            if (Strings.isStringLiteralSeparator(part.charAt(i))) {
                if (i == 0) {
                    // The first char is a string literal separator
                    return -1;
                }
                stringLiteral = !stringLiteral;
            } else {
                if (!stringLiteral && part.charAt(i) == '=') {
                    return i;
                }
            }
        }
        return -1;
    }

}
