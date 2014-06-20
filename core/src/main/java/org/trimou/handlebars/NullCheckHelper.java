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

/**
 * Renders a block if the param is/isn't null.
 *
 * <pre>
 * {{#isNull item.price}}
 *   It's null!
 * {{/isNull}}
 * </pre>
 *
 * <p>
 * Multiple params may be evaluated. The default evaluation logic is
 * conjunction:
 * </p>
 *
 * <pre>
 * {{#isNull item.active item.valid}}
 *   All are null.
 * {{/isNull}}
 * </pre>
 *
 * <p>
 * The evaluation logic may be specified:
 * </p>
 *
 * <pre>
 * {{#isNull item.active item.valid logic="or"}}
 *   At least one is null.
 * {{/isNull}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class NullCheckHelper extends MatchingSectionHelper {

    private boolean testNotNull;

    /**
     * Test null.
     */
    public NullCheckHelper() {
        this(false);
    }

    /**
     *
     * @param testNotNull
     */
    public NullCheckHelper(boolean testNotNull) {
        this.testNotNull = testNotNull;
    }

    @Override
    protected boolean isMatching(Object value) {
        return testNotNull ? value != null : value == null;
    }

}
