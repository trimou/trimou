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
package org.trimou.engine.interpolation;

import org.trimou.engine.config.ConfigurationAware;
import org.trimou.engine.segment.ValueSegment;

/**
 * This component handles variable miss during interpolation.
 *
 * @author Martin Kouba
 */
public interface MissingValueHandler extends ConfigurationAware {

    /**
     *
     * @param valueSegmentInfo
     * @return the replacement to process, may be <code>null</code>
     */
    public Object handle(ValueSegmentInfo valueSegmentInfo);

    /**
     * Info about corresponding {@link ValueSegment}.
     */
    public interface ValueSegmentInfo {

        /**
         * @return the key
         */
        public String getKey();

        /**
         * @return the original line where the segment comes from
         */
        public int getLine();

        /**
         * @return the template name
         */
        public String getTemplateName();

        /**
         * @return <code>true</code> if the original value should not be
         *         escaped, <code>false</code> otherwise
         */
        public boolean isUnescape();

    }

}
