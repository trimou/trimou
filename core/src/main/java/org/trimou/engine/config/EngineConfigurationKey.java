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

import org.trimou.engine.interpolation.MissingValueHandler;
import org.trimou.engine.segment.LineSeparatorSegment;
import org.trimou.handlebars.Helper;

/**
 * Engine configuration keys.
 *
 * @author Martin Kouba
 */
public enum EngineConfigurationKey implements ConfigurationKey {

    /**
     * The default start delimiter (e.g. <code>{{</code>).
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
     *
     * @deprecated see also {@link MissingValueHandler}
     */
    @Deprecated
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
    TEMPLATE_RECURSIVE_INVOCATION_LIMIT(10),
    /**
     * If set to <code>true</code> interpolated values are never escaped, i.e.
     * org.trimou.engine.text.TextSupport.escapeHtml(String) is never called.
     */
    SKIP_VALUE_ESCAPING(false),
    /**
     * The encoding every template locator should use if reading template from a
     * file.
     */
    DEFAULT_FILE_ENCODING(SecurityActions.getSystemProperty("file.encoding")),
    /**
     * If set to <code>true</code> the template cache is enabled.
     */
    TEMPLATE_CACHE_ENABLED(true),
    /**
     * The template cache expiration timeout in seconds. Zero and negative
     * values mean no timeout.
     *
     * @see com.google.common.cache.CacheBuilder#expireAfterWrite(long,
     *      java.util.concurrent.TimeUnit)
     */
    TEMPLATE_CACHE_EXPIRATION_TIMEOUT(0l),
    /**
     * If set to <code>true</code> handlebars-like helpers are supported.
     *
     * @see Helper
     */
    HANDLEBARS_SUPPORT_ENABLED(true),
    /**
     * If set to <code>true</code> line separators will be reused within
     * template to conserve memory. Note that
     * {@link LineSeparatorSegment#getOrigin()} will not display the correct
     * info.
     */
    REUSE_LINE_SEPARATOR_SEGMENTS(true),
    /**
     * The alias for iteration metadata object available inside an iteration
     * block.
     *
     * <code>
     * {{#each items}}
     *  {{iter.index}}
     * {{/each}}
     * </code>
     */
    ITERATION_METADATA_ALIAS("iter"),
    /**
     * If set to <code>true</code> the evaluation of simple variables, e.g.
     * <code>{{.}}</code> or <code>{{foo}}</code>, is optimized.
     */
    RESOLVER_HINTS_ENABLED(true), ;

    private Object defaultValue;

    private String key;

    EngineConfigurationKey(Object defaultValue) {
        this.key = ConfigurationProperties.buildPropertyKey(this.toString(),
                new String[] { EngineConfigurationKey.class.getPackage()
                        .getName() });
        this.defaultValue = defaultValue;
    }

    public String get() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
