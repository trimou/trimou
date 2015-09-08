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
package org.trimou.engine.interpolation;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.util.Patterns;
import org.trimou.util.Strings;

/**
 * The default {@link LiteralSupport} implementation. The supported literals are
 * strings and integers.
 *
 * @author Martin Kouba
 */
public class DefaultLiteralSupport extends AbstractConfigurationAware
        implements LiteralSupport {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultLiteralSupport.class);

    private final Pattern integerLiteralPattern = Patterns
            .newHelperIntegerLiteralPattern();

    private final Pattern longLiteralPattern = Patterns
            .newHelperLongLiteralPattern();

    @Override
    public Object getLiteral(String value, MustacheTagInfo tagInfo) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        Object literal = null;
        if (Strings.isStringLiteralSeparator(value.charAt(0))) {
            literal = value.substring(1, value.length() - 1);
        } else if (integerLiteralPattern.matcher(value).matches()) {
            try {
                literal = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Unable to parse integer literal: " + value, e);
            }
        } else if (longLiteralPattern.matcher(value).matches()) {
            try {
                literal = Long
                        .parseLong(value.substring(0, value.length() - 1));
            } catch (NumberFormatException e) {
                logger.warn("Unable to parse long literal: " + value, e);
            }
        }
        return literal;
    }

}
