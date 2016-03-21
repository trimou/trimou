/*
 * Copyright 2014 Martin Kouba
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

import java.util.Optional;
import java.util.Set;

/**
 * <code>
 * {{isOdd iterIndex "oddRow"}}
 * </code>
 *
 * <code>
 * {{isOdd iterIndex "oddRow" "evenRow"}}
 * </code>
 *
 * <code>
 * {{#isOdd iterIndex}}
 * ...
 * {{/isEven}}
 * </code>
 *
 * @author Martin Kouba
 */
public class NumberIsOddHelper extends NumberMatchingHelper {

    @Override
    protected boolean isMatching(Number value) {
        return value.intValue() % 2 != 0;
    }

    @Override
    protected Optional<Set<String>> getSupportedHashKeys() {
        return NO_SUPPORTED_HASH_KEYS;
    }

}