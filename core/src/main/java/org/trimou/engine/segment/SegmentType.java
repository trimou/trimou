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

import org.trimou.annotations.Internal;
import org.trimou.engine.MustacheTagType;
import org.trimou.util.Checker;

/**
 * Type of segment.
 */
@Internal
public enum SegmentType {

    ROOT(null),
    // Note that one segment is used for both a variable and an unescape variable
    VALUE(MustacheTagType.VARIABLE),
    TEXT(null),
    SECTION(MustacheTagType.SECTION),
    INVERTED_SECTION(MustacheTagType.INVERTED_SECTION),
    COMMENT(MustacheTagType.COMMENT),
    LINE_SEPARATOR(null),
    DELIMITERS(MustacheTagType.DELIMITER),
    PARTIAL(MustacheTagType.PARTIAL),
    // Spec extensions
    EXTEND(MustacheTagType.EXTEND),
    EXTEND_SECTION(MustacheTagType.EXTEND_SECTION);

    private MustacheTagType tagType;

    SegmentType(MustacheTagType tagType) {
        this.tagType = tagType;
    }

    /**
     * @return the corresponding tag type or <code>null</code>
     */
    MustacheTagType getTagType() {
        return tagType;
    }

    public static SegmentType fromTag(MustacheTagType tagType) {
        Checker.checkArgumentNotNull(tagType);
        for (SegmentType type : values()) {
            if(tagType.equals(type.getTagType())) {
                return type;
            }
        }
        if(MustacheTagType.UNESCAPE_VARIABLE.equals(tagType)) {
            return VALUE;
        }
        throw new IllegalStateException("Unsupported tag type");
    }


}
