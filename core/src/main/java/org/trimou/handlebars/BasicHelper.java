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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.trimou.engine.MustacheTagType;

/**
 * Basic validating helper.
 *
 * @author Martin Kouba
 */
public abstract class BasicHelper extends AbstractHelper {

    protected static final Optional<Set<String>> NO_SUPPORTED_HASH_KEYS = Optional
            .of(Collections.<String> emptySet());

    protected static final MustacheTagType[] HELPER_TAG_TYPES = new MustacheTagType[] {
            MustacheTagType.SECTION, MustacheTagType.VARIABLE,
            MustacheTagType.UNESCAPE_VARIABLE };

    @Override
    public void validate(HelperDefinition definition) {
        Class<? extends Helper> helperClazz = this.getClass();
        HelperValidator.checkType(helperClazz, definition, allowedTagTypes());
        HelperValidator.checkParams(helperClazz, definition,
                numberOfRequiredParameters());
        HelperValidator.checkHash(definition, this);
    }

    protected MustacheTagType[] allowedTagTypes() {
        return HELPER_TAG_TYPES;
    }

    protected int numberOfRequiredParameters() {
        return 1;
    }

    protected int numberOfRequiredHashEntries() {
        return 0;
    }

    /**
     *
     * @return the supported hash keys
     */
    protected Optional<Set<String>> getSupportedHashKeys() {
        return Optional.empty();
    }

}
