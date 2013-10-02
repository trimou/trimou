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
package org.trimou.engine;

import static org.trimou.util.Checker.checkArgumentNotEmpty;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationFactory;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.listener.MustacheCompilationEvent;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.listener.MustacheParsingEvent;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.engine.parser.ParserFactory;
import org.trimou.engine.parser.ParsingHandler;
import org.trimou.engine.parser.ParsingHandlerFactory;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Strings;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Default Mustache engine.
 *
 * @author Martin Kouba
 */
class DefaultMustacheEngine implements MustacheEngine,
        RemovalListener<String, Mustache> {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultMustacheEngine.class);

    private LoadingCache<String, Optional<Mustache>> templateCache;

    private Configuration configuration;

    private ParserFactory parserFactory;

    private ParsingHandlerFactory parsingHandlerFactory;

    /**
     * Make this type proxyable (CDI) so that it's possible to produce
     * application scoped CDI bean
     */
    DefaultMustacheEngine() {
    }

    /**
     *
     * @param builder
     */
    DefaultMustacheEngine(MustacheEngineBuilder builder) {

        // First create the engine configuration
        configuration = new ConfigurationFactory().createConfiguration(builder);

        if (configuration
                .getBooleanPropertyValue(EngineConfigurationKey.DEBUG_MODE)) {
            logger.warn("Attention! Debug mode enabled: template cache disabled, additional logging enabled");
        } else {

            if (configuration
                    .getBooleanPropertyValue(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED)) {
                buildTemplateCache();
                if (configuration
                        .getBooleanPropertyValue(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES)) {
                    precompileTemplates();
                }
            } else {
                logger.info("Template cache explicitly disabled!");
            }
        }

        this.parserFactory = new ParserFactory();
        this.parsingHandlerFactory = new ParsingHandlerFactory();
    }

    public Mustache getMustache(String templateId) {
        checkArgumentNotEmpty(templateId);
        return templateCache != null ? getTemplateFromCache(templateId)
                : locateAndParse(templateId);
    }

    public Mustache compileMustache(String templateId, String templateContent) {
        checkArgumentNotEmpty(templateId);
        checkArgumentNotEmpty(templateContent);
        return parse(templateId, new StringReader(templateContent));
    }

    /**
     * @return
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Invalidates the template cache.
     */
    public void invalidateTemplateCache() {
        this.templateCache.invalidateAll();
    }

    /**
     *
     * @return
     */
    public LoadingCache<String, Optional<Mustache>> getTemplateCache() {
        return templateCache;
    }

    @Override
    public void onRemoval(RemovalNotification<String, Mustache> notification) {
        logger.debug("Removed template [templateId: {}, cause: {}]",
                notification.getKey(), notification.getCause());
    }

    private void buildTemplateCache() {

        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        long expirationTimeout = configuration
                .getLongPropertyValue(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT);

        if (expirationTimeout > 0) {
            logger.info("Template cache expiration timeout set: {} seconds",
                    expirationTimeout);
            cacheBuilder.expireAfterWrite(expirationTimeout, TimeUnit.SECONDS);
            cacheBuilder.removalListener(this);
        }

        templateCache = cacheBuilder
                .build(new CacheLoader<String, Optional<Mustache>>() {

                    @Override
                    public Optional<Mustache> load(String key) throws Exception {
                        return Optional.fromNullable(locateAndParse(key));
                    }
                });
    }

    private void precompileTemplates() {

        Set<String> templateNames = new HashSet<String>();

        for (TemplateLocator locator : configuration.getTemplateLocators()) {
            templateNames.addAll(locator.getAllIdentifiers());
        }

        for (String templateName : templateNames) {
            getTemplateFromCache(templateName);
        }
    }

    /**
     *
     * @param templateId
     * @param reader
     * @return
     */
    private Mustache parse(String templateId, Reader reader) {

        ParsingHandler handler = parsingHandlerFactory.createParsingHandler();

        reader = notifyListenersBeforeParsing(templateId, reader);
        parserFactory.createParser(this).parse(templateId, reader, handler);
        Mustache mustache = handler.getCompiledTemplate();
        notifyListenersAfterCompilation(mustache);

        return mustache;
    }

    /**
     *
     * @param templateId
     * @return
     */
    private Reader locate(String templateId) {

        if (configuration.getTemplateLocators() == null
                || configuration.getTemplateLocators().isEmpty()) {
            return null;
        }

        Reader reader = null;

        for (TemplateLocator locator : configuration.getTemplateLocators()) {
            reader = locator.locate(templateId);
            if (reader != null) {
                break;
            }
        }
        return reader;
    }

    /**
     *
     * @param templateId
     * @return
     */
    private Mustache locateAndParse(String templateId) {
        Reader reader = locate(templateId);
        return reader != null ? parse(templateId, reader) : null;
    }

    private Reader notifyListenersBeforeParsing(String templateName,
            Reader reader) {
        if (configuration.getMustacheListeners() != null) {
            MustacheParsingEvent event = new DefaultMustacheParsingEvent(
                    templateName, reader);
            for (MustacheListener listener : configuration
                    .getMustacheListeners()) {
                listener.parsingStarted(event);
            }
            return event.getMustacheContents();
        }
        return reader;
    }

    private void notifyListenersAfterCompilation(Mustache mustache) {
        if (configuration.getMustacheListeners() != null) {
            MustacheCompilationEvent event = new DefaultMustacheCompilationEvent(
                    mustache);
            for (MustacheListener listener : configuration
                    .getMustacheListeners()) {
                listener.compilationFinished(event);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(this.getClass().getName());
        builder.append("]");
        builder.append(Strings.LINE_SEPARATOR);
        builder.append(configuration.toString());
        return builder.toString();
    }

    private Mustache getTemplateFromCache(String templateName) {
        try {
            return templateCache.get(templateName).orNull();
        } catch (ExecutionException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
    }

    /**
     *
     * @author Martin Kouba
     */
    private static class DefaultMustacheCompilationEvent implements
            MustacheCompilationEvent {

        private final Mustache mustache;

        /**
         *
         * @param mustache
         */
        public DefaultMustacheCompilationEvent(Mustache mustache) {
            super();
            this.mustache = mustache;
        }

        @Override
        public Mustache getMustache() {
            return mustache;
        }

    }

    /**
     *
     * @author Martin Kouba
     */
    private static class DefaultMustacheParsingEvent implements
            MustacheParsingEvent {

        private final String mustacheName;

        private Reader reader;

        /**
         *
         * @param mustacheName
         */
        public DefaultMustacheParsingEvent(String mustacheName, Reader reader) {
            super();
            this.mustacheName = mustacheName;
            this.reader = reader;
        }

        public String getMustacheName() {
            return mustacheName;
        }

        @Override
        public Reader getMustacheContents() {
            return reader;
        }

        @Override
        public void setMustacheContents(Reader reader) {
            this.reader = reader;
        }

    }

}
