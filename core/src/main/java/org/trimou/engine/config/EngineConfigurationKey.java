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
package org.trimou.engine.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Engine configuration keys.
 *
 * @author Martin Kouba
 */
public enum EngineConfigurationKey implements ConfigurationKey {

    /**
     * The default start delimiter (e.g. <code>}}</code>).
     */
    START_DELIMITER("{{"),
    /**
     * The default end delimiter (e.g. <code>}}</code>).
     */
    END_DELIMITER("}}"),
    /**
     * <code>true</code> if precompilation of all available templates is
     * required, <code>false</code> otherwise.
     */
    PRECOMPILE_ALL_TEMPLATES(false),
    /**
     * <code>true</code> if standalone lines should be removed (see also
     * Mustache spec), <code>false</code> otherwise.
     */
    REMOVE_STANDALONE_LINES(true),
    /**
     * <code>true</code> if unnecessary segments should be removed (e.g.
     * comments), <code>false</code> otherwise.
     */
    REMOVE_UNNECESSARY_SEGMENTS(true),
    /**
     * <code>true</code> if lookup miss should result in exception,
     * <code>false</code> otherwise.
     */
    NO_VALUE_INDICATES_PROBLEM(false),
    /**
     * <code>true</code> in case of debug mode should be enabled,
     * <code>false</code> otherwise. Debug mode disables the template cache and
     * provides some more logging during template rendering.
     * */
    DEBUG_MODE(false),
    /**
     * <code>true</code> in case of the section-based literal blocks should be
     * cached (useful to optimize some lambdas processing scenarios, but memory
     * intensive), <code>false</code> otherwise.
     */
    CACHE_SECTION_LITERAL_BLOCK(false),
    /**
     * The limit of recursive template invocation; 0 - recursive invocation is
     * forbidden.
     */
    TEMPLATE_RECURSIVE_INVOCATION_LIMIT(10), ;

    private Object defaultValue;

    private String key;

    EngineConfigurationKey(Object defaultValue) {
        this.key = EngineConfigurationKey.class.getPackage().getName()
                + "."
                + WordUtils.uncapitalize(StringUtils.replace(
                        WordUtils.capitalizeFully(this.toString(), '_'), "_",
                        ""));
        this.defaultValue = defaultValue;
    }

    public String get() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

}
