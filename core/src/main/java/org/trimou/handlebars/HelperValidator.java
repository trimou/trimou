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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheTagType;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Arrays;
import org.trimou.util.Checker;

/**
 *
 * @author Martin Kouba
 * @since 1.5
 */
public final class HelperValidator {

    private static final Logger LOGGER = LoggerFactory
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
    public static void checkParams(Class<? extends Helper> helperClazz,
            HelperDefinition definition, int paramSize) {
        Checker.checkArgumentNotNull(definition);
        Checker.checkArgument(paramSize >= 0,
                "Helper may only require zero or more params");

        int size = definition.getParameters().size();

        if (size < paramSize) {
            throw newValidationException(String.format(
                    "Insufficient number of parameters - expected: %s, current: %s",
                    paramSize, size), helperClazz, definition);
        }
        if (size > paramSize) {
            LOGGER.trace(
                    "{} superfluous parameters detected [helper: {}, template: {}, line: {}]",
                    size - paramSize, helperClazz.getName(),
                    definition.getTagInfo().getTemplateName(),
                    definition.getTagInfo().getLine());
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
    public static void checkType(Class<? extends Helper> helperClazz,
            HelperDefinition definition, MustacheTagType... allowedTypes) {
        Checker.checkArgumentsNotNull(definition, allowedTypes);
        if (!Arrays.contains(allowedTypes, definition.getTagInfo().getType())) {
            throw newValidationException("Unsupported tag type", helperClazz,
                    definition);
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
    public static void checkHash(Class<? extends Helper> helperClazz,
            HelperDefinition definition, int hashSize) {
        Checker.checkArgumentNotNull(definition);
        Checker.checkArgument(hashSize >= 0,
                "Helper may only require zero or more hash entries");

        int size = definition.getHash().size();

        if (size < hashSize) {
            throw newValidationException(String.format(
                    "Insufficient number of hash entries - expected: %s, current: %s",
                    hashSize, size), helperClazz, definition);
        }
        if (size > hashSize) {
            LOGGER.trace(
                    "{} superfluous hash entries detected [helper: {}, template: {}, line: {}]",
                    size - hashSize, helperClazz.getName(),
                    definition.getTagInfo().getTemplateName(),
                    definition.getTagInfo().getLine());
        }
    }

    /**
     *
     * @param definition
     * @param hashSize
     * @see #checkHash(Class, HelperDefinition, int)
     */
    public static void checkHash(HelperDefinition definition,
            BasicHelper helper) {
        // Number of required hash entries
        checkHash(helper.getClass(), definition,
                helper.numberOfRequiredHashEntries());
        if (!helper.getRequiredHashKeys().isEmpty()) {
            // Check required hash keys
            for (String key : helper.getRequiredHashKeys()) {
                if (!definition.getHash().containsKey(key)) {
                    throw newValidationException(String
                            .format("Required hash key %s not found", key),
                            helper.getClass(), definition);
                }
            }
        }
        if (definition.getHash().isEmpty()
                || BasicHelper.ANY_HASH_KEY_SUPPORTED
                        .equals(helper.getSupportedHashKeys())) {
            return;
        }
        // Log a warning message if an unsupported hash key is found
        Set<String> supportedHashKeys = helper.getSupportedHashKeys();
        for (String key : definition.getHash().keySet()) {
            if (!supportedHashKeys.contains(key)) {
                LOGGER.info(
                        "Unsupported hash key detected [key: {}, helper: {}, template: {}, line: {}]",
                        key, helper.getClass().getName(),
                        definition.getTagInfo().getTemplateName(),
                        definition.getTagInfo().getLine());
            }
        }
    }

    public static MustacheException newValidationException(String msg,
            Class<? extends Helper> helperClazz, HelperDefinition definition) {
        return new MustacheException(
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                msg + " [helper: %s, template: %s, line: %s]",
                helperClazz.getName(),
                definition.getTagInfo().getTemplateName(),
                definition.getTagInfo().getLine());
    }

}
