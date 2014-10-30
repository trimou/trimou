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
package org.trimou.engine.segment;

import org.trimou.engine.context.ExecutionContext;
import org.trimou.handlebars.Helper;
import org.trimou.handlebars.Options;

/**
 * A {@link Segment} which may have a {@link Helper} associated.
 *
 * @author Martin Kouba
 */
interface HelperAwareSegment extends Segment {

    /**
     *
     * @param appendable
     * @param context
     * @see Options#fn()
     * @see Options#fn(Appendable)
     */
    void fn(Appendable appendable, ExecutionContext context);

}
