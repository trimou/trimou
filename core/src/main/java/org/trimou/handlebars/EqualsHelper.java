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
 * Conditionally renders a block if the first param does/doesn't equal to the
 * second param (by means of {@link Object#equals(Object)}).
 *
 * <pre>
 * {{#isEq item.status Status.ACTIVE}}
 *   It's equal!
 * {{/isEq}}
 * </pre>
 *
 * If only one param is defined, the object at the top of the context stack is used:
 *
 * <pre>
 * {{#with foo}}
 *  {{#isEq Status.ACTIVE}}
 *      It's equal!
 *  {{/isEq}}
 * {{/with}}
 * </pre>
 *
 * If any object is null, the block is not rendered.
 *
 * @author Martin Kouba
 */
public class EqualsHelper extends BasicSectionHelper {

    private final boolean testInequality;

    /**
     * Tests equality.
     */
    public EqualsHelper() {
        this(false);
    }

    /**
     *
     * @param testInequality
     */
    public EqualsHelper(boolean testInequality) {
        this.testInequality = testInequality;
    }

    @Override
    public void execute(Options options) {

        Object obj1, obj2;

        if (options.getParameters().size() == 1) {
            obj1 = options.peek();
            obj2 = options.getParameters().get(0);
        } else {
            obj1 = options.getParameters().get(0);
            obj2 = options.getParameters().get(1);
        }

        if (obj1 == null || obj2 == null) {
            return;
        }

        if (testInequality ? !obj1.equals(obj2) : obj1.equals(obj2)) {
            options.fn();
        }
    }

}
