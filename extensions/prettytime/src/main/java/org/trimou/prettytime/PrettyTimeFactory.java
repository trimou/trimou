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
package org.trimou.prettytime;

import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

/**
 * Might be useful to customize the set of time units. See also <a href=
 * "http://ocpsoft.org/support/topic/5-minutes-is-default-minimum-duration/">5
 * minutes is default minimum duration?</a>
 *
 * @author Martin Kouba
 */
@FunctionalInterface
public interface PrettyTimeFactory {

    /**
     *
     * @return a new instance of {@link PrettyTime} for the given locale
     */
    PrettyTime createPrettyTime(Locale locale);

}
