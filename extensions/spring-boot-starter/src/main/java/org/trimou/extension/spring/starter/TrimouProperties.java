/*
 * Copyright 2017 Trimou Team
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

package org.trimou.extension.spring.starter;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.spring4.web.SpringResourceTemplateLocator;
import org.trimou.util.Strings;

/**
 * {@link ConfigurationProperties} for Trimou
 */
@ConfigurationProperties(prefix = TrimouProperties.PROPERTY_PREFIX)
public final class TrimouProperties extends AbstractTemplateViewResolverProperties {

    static final String PROPERTY_PREFIX = "trimou";
    private static final Logger LOGGER = LoggerFactory.getLogger(TrimouProperties.class);

    /**
     * Prefix to apply to template names.
     */
    private String prefix = SpringResourceTemplateLocator.DEFAULT_PREFIX;

    /**
     * Suffix to apply to template names.
     */
    private String suffix = SpringResourceTemplateLocator.DEFAULT_SUFFIX;

    /**
     * Start delimiter
     */
    private String startDelimiter = getDefaultStringValue(EngineConfigurationKey.START_DELIMITER);

    /**
     * End delimiter
     */
    private String endDelimiter = getDefaultStringValue(EngineConfigurationKey.END_DELIMITER);

    /**
     * Precompile all templates
     */
    private boolean precompileTemplates = getDefaultBooleanValue(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES);

    /**
     * Remove standalone lines
     */
    private boolean removeStandaloneLines = getDefaultBooleanValue(EngineConfigurationKey.REMOVE_STANDALONE_LINES);

    /**
     * Remove unnecessary segments
     */
    private boolean removeUnnecessarySegments =
            getDefaultBooleanValue(EngineConfigurationKey.REMOVE_UNNECESSARY_SEGMENTS);

    /**
     * Debug mode
     */
    private boolean debugMode = getDefaultBooleanValue(EngineConfigurationKey.DEBUG_MODE);

    /**
     * Cache section-based literal blocks
     */
    private boolean cacheSectionLiteralBlock =
            getDefaultBooleanValue(EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK);

    /**
     * Template recursive invocation limit
     */
    private int templateRecursiveInvocationLimit =
            getDefaultIntegerValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT);

    /**
     * Skip value escaping
     */
    private boolean skipValueEscaping = getDefaultBooleanValue(EngineConfigurationKey.SKIP_VALUE_ESCAPING);

    /**
     * Template cache expiration timeout
     */
    private long templateCacheExpirationTimeout =
            getDefaultLongValue(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT);

    /**
     * Enable helper
     */
    private boolean enableHelper = getDefaultBooleanValue(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED);

    /**
     * Reuse line separator segments
     */
    private boolean reuseLineSeparatorSegments =
            getDefaultBooleanValue(EngineConfigurationKey.REUSE_LINE_SEPARATOR_SEGMENTS);

    /**
     * Iteration meta-data alias
     */
    private String iterationMetadataAlias = getDefaultStringValue(EngineConfigurationKey.ITERATION_METADATA_ALIAS);

    /**
     * Enable resolver hints
     */
    private boolean enableResolverHints = getDefaultBooleanValue(EngineConfigurationKey.RESOLVER_HINTS_ENABLED);

    /**
     * Enable nested template support
     */
    private boolean enableNestedTemplates =
            getDefaultBooleanValue(EngineConfigurationKey.NESTED_TEMPLATE_SUPPORT_ENABLED);

    /**
     * Cache template sources
     */
    private boolean cacheTemplateSources =
            getDefaultBooleanValue(EngineConfigurationKey.TEMPLATE_CACHE_USED_FOR_SOURCE);

    public TrimouProperties() {
        super(SpringResourceTemplateLocator.DEFAULT_PREFIX, SpringResourceTemplateLocator.DEFAULT_SUFFIX);
        final boolean cacheEnabled = getDefaultBooleanValue(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED);
        setCache(cacheEnabled);
        if (getCharset() == null) {
            setCharset(StandardCharsets.UTF_8);
        }
    }

    private static boolean getDefaultBooleanValue(final EngineConfigurationKey key) {
        final Object object = key.getDefaultValue();
        if (object != null) {
            final String value = String.valueOf(object);
            if (!Strings.isEmpty(value)) {
                return Boolean.valueOf(value);
            }
        }
        LOGGER.warn("Unable to decode key '{}' as boolean. Return false", key);
        return false;
    }

    private static String getDefaultStringValue(final EngineConfigurationKey key) {
        final Object object = key.getDefaultValue();
        if (object != null) {
            return String.valueOf(object);
        }
        LOGGER.warn("Unable to decode key '{}' as string. Return null", key);
        return null;
    }

    private static int getDefaultIntegerValue(final EngineConfigurationKey key) {
        final Object object = key.getDefaultValue();
        if (object != null) {
            final String value = String.valueOf(object);
            if (!Strings.isEmpty(value)) {
                return Integer.parseInt(value);
            }
        }
        LOGGER.warn("Unable to decode key '{}' as int. Return 0", key);
        return 0;
    }

    private static long getDefaultLongValue(final EngineConfigurationKey key) {
        final Object object = key.getDefaultValue();
        if (object != null) {
            final String value = String.valueOf(object);
            if (!Strings.isEmpty(value)) {
                return Long.parseLong(value);
            }
        }
        LOGGER.warn("Unable to decode key '{}' as long. Return 0L", key);
        return 0L;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public String getStartDelimiter() {
        return startDelimiter;
    }

    public void setStartDelimiter(final String startDelimiter) {
        this.startDelimiter = startDelimiter;
    }

    public String getEndDelimiter() {
        return endDelimiter;
    }

    public void setEndDelimiter(final String endDelimiter) {
        this.endDelimiter = endDelimiter;
    }

    public boolean isPrecompileTemplates() {
        return precompileTemplates;
    }

    public void setPrecompileTemplates(final boolean precompileTemplates) {
        this.precompileTemplates = precompileTemplates;
    }

    public boolean isRemoveStandaloneLines() {
        return removeStandaloneLines;
    }

    public void setRemoveStandaloneLines(final boolean removeStandaloneLines) {
        this.removeStandaloneLines = removeStandaloneLines;
    }

    public boolean isRemoveUnnecessarySegments() {
        return removeUnnecessarySegments;
    }

    public void setRemoveUnnecessarySegments(final boolean removeUnnecessarySegments) {
        this.removeUnnecessarySegments = removeUnnecessarySegments;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(final boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isCacheSectionLiteralBlock() {
        return cacheSectionLiteralBlock;
    }

    public void setCacheSectionLiteralBlock(final boolean cacheSectionLiteralBlock) {
        this.cacheSectionLiteralBlock = cacheSectionLiteralBlock;
    }

    public int getTemplateRecursiveInvocationLimit() {
        return templateRecursiveInvocationLimit;
    }

    public void setTemplateRecursiveInvocationLimit(final int templateRecursiveInvocationLimit) {
        this.templateRecursiveInvocationLimit = templateRecursiveInvocationLimit;
    }

    public boolean isSkipValueEscaping() {
        return skipValueEscaping;
    }

    public void setSkipValueEscaping(final boolean skipValueEscaping) {
        this.skipValueEscaping = skipValueEscaping;
    }

    public long getTemplateCacheExpirationTimeout() {
        return templateCacheExpirationTimeout;
    }

    public void setTemplateCacheExpirationTimeout(final long templateCacheExpirationTimeout) {
        this.templateCacheExpirationTimeout = templateCacheExpirationTimeout;
    }

    public boolean isEnableHelper() {
        return enableHelper;
    }

    public void setEnableHelper(final boolean enableHelper) {
        this.enableHelper = enableHelper;
    }

    public boolean isReuseLineSeparatorSegments() {
        return reuseLineSeparatorSegments;
    }

    public void setReuseLineSeparatorSegments(final boolean reuseLineSeparatorSegments) {
        this.reuseLineSeparatorSegments = reuseLineSeparatorSegments;
    }

    public String getIterationMetadataAlias() {
        return iterationMetadataAlias;
    }

    public void setIterationMetadataAlias(final String iterationMetadataAlias) {
        this.iterationMetadataAlias = iterationMetadataAlias;
    }

    public boolean isEnableResolverHints() {
        return enableResolverHints;
    }

    public void setEnableResolverHints(final boolean enableResolverHints) {
        this.enableResolverHints = enableResolverHints;
    }

    public boolean isEnableNestedTemplates() {
        return enableNestedTemplates;
    }

    public void setEnableNestedTemplates(final boolean enableNestedTemplates) {
        this.enableNestedTemplates = enableNestedTemplates;
    }

    public boolean isCacheTemplateSources() {
        return cacheTemplateSources;
    }

    public void setCacheTemplateSources(final boolean cacheTemplateSources) {
        this.cacheTemplateSources = cacheTemplateSources;
    }

    /**
     * Apply the {@link TrimouProperties} to a {@link MustacheEngineBuilder}.
     *
     * @param engineBuilder the Trimou mustache engine builder to apply the properties to
     */
    public void applyToTrimouMustacheEngineBuilder(final MustacheEngineBuilder engineBuilder) {
        engineBuilder
                .setProperty(EngineConfigurationKey.START_DELIMITER, getStartDelimiter())
                .setProperty(EngineConfigurationKey.END_DELIMITER, getEndDelimiter())
                .setProperty(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES, isPrecompileTemplates())
                .setProperty(EngineConfigurationKey.REMOVE_STANDALONE_LINES, isRemoveStandaloneLines())
                .setProperty(EngineConfigurationKey.REMOVE_UNNECESSARY_SEGMENTS, isRemoveUnnecessarySegments())
                .setProperty(EngineConfigurationKey.DEBUG_MODE, isDebugMode())
                .setProperty(EngineConfigurationKey.CACHE_SECTION_LITERAL_BLOCK, isCacheSectionLiteralBlock())
                .setProperty(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        getTemplateRecursiveInvocationLimit())
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, isSkipValueEscaping())
                .setProperty(EngineConfigurationKey.DEFAULT_FILE_ENCODING, getCharset().name())
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED, isCache())
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT,
                        getTemplateCacheExpirationTimeout())
                .setProperty(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED, isEnableHelper())
                .setProperty(EngineConfigurationKey.REUSE_LINE_SEPARATOR_SEGMENTS, isReuseLineSeparatorSegments())
                .setProperty(EngineConfigurationKey.ITERATION_METADATA_ALIAS, getIterationMetadataAlias())
                .setProperty(EngineConfigurationKey.RESOLVER_HINTS_ENABLED, isEnableResolverHints())
                .setProperty(EngineConfigurationKey.NESTED_TEMPLATE_SUPPORT_ENABLED, isEnableNestedTemplates())
                .setProperty(EngineConfigurationKey.TEMPLATE_CACHE_USED_FOR_SOURCE, isCacheTemplateSources());
    }
}
