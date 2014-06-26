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

import java.io.IOException;
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

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.io.CharStreams;

/**
 * The default Mustache engine implementation.
 *
 * @author Martin Kouba
 */
class DefaultMustacheEngine implements MustacheEngine {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultMustacheEngine.class);

    private final LoadingCache<String, Optional<Mustache>> templateCache;

    private final LoadingCache<String, Optional<String>> sourceCache;

    private final Configuration configuration;

    private final ParserFactory parserFactory;

    private final ParsingHandlerFactory parsingHandlerFactory;

    /**
     * Workaround for CDI (JSR 299, JSR 346) - make this type proxyable so that
     * it's possible to produce application-scoped CDI bean.
     */
    DefaultMustacheEngine() {
        configuration = null;
        parserFactory = null;
        parsingHandlerFactory = null;
        templateCache = null;
        sourceCache = null;
    }

    /**
     *
     * @param builder
     */
    DefaultMustacheEngine(MustacheEngineBuilder builder) {
        // First create the engine configuration
        configuration = new ConfigurationFactory().createConfiguration(builder);
        parserFactory = new ParserFactory();
        parsingHandlerFactory = new ParsingHandlerFactory();

        if (configuration
                .getBooleanPropertyValue(EngineConfigurationKey.DEBUG_MODE)) {
            templateCache = null;
            sourceCache = null;
            logger.warn("Attention! Debug mode enabled: template cache disabled, additional logging enabled");
        } else {

            if (configuration
                    .getBooleanPropertyValue(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED)) {
                templateCache = buildTemplateCache();
                sourceCache = buildSourceCache();
                if (configuration
                        .getBooleanPropertyValue(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES)) {
                    precompileTemplates();
                }
            } else {
                templateCache = null;
                sourceCache = null;
                logger.info("Template cache explicitly disabled!");
            }
        }
    }

    public Mustache getMustache(String templateId) {
        checkArgumentNotEmpty(templateId);
        return templateCache != null ? getTemplateFromCache(templateId)
                : locateAndParse(templateId);
    }

    public String getMustacheSource(String templateId) {
        checkArgumentNotEmpty(templateId);
        return sourceCache != null ? getSourceFromCache(templateId)
                : locateAndRead(templateId);
    }

    public Mustache compileMustache(String templateId, String templateContent) {
        checkArgumentNotEmpty(templateId);
        checkArgumentNotEmpty(templateContent);
        return parse(templateId, new StringReader(templateContent));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void invalidateTemplateCache() {
        if (templateCache == null) {
            logger.warn("Unable to invalidate the template cache - it's disabled!");
            return;
        }
        templateCache.invalidateAll();
        sourceCache.invalidateAll();
    }

    private LoadingCache<String, Optional<Mustache>> buildTemplateCache() {
        return buildCache("Template",
                new CacheLoader<String, Optional<Mustache>>() {
                    @Override
                    public Optional<Mustache> load(String key) throws Exception {
                        return Optional.fromNullable(locateAndParse(key));
                    }
                }, new RemovalListener<String, Optional<Mustache>>() {
                    @Override
                    public void onRemoval(
                            RemovalNotification<String, Optional<Mustache>> notification) {
                        logger.debug(
                                "Removed template from cache [templateId: {}, cause: {}]",
                                notification.getKey(), notification.getCause());
                    }
                });
    }

    /**
     * Properties of the source cache are dependent on that of the template
     * cache.
     */
    private LoadingCache<String, Optional<String>> buildSourceCache() {
        return buildCache("Source",
                new CacheLoader<String, Optional<String>>() {
                    @Override
                    public Optional<String> load(String key) throws Exception {
                        return Optional.fromNullable(locateAndRead(key));
                    }
                }, new RemovalListener<String, Optional<String>>() {
                    @Override
                    public void onRemoval(
                            RemovalNotification<String, Optional<String>> notification) {
                        logger.debug(
                                "Removed template source from cache [templateId: {}, cause: {}]",
                                notification.getKey(), notification.getCause());
                    }
                });
    }

    private <K, V> LoadingCache<K, V> buildCache(String name,
            CacheLoader<K, V> cacheLoader, RemovalListener<K, V> removalListener) {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        long expirationTimeout = configuration
                .getLongPropertyValue(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT);

        if (expirationTimeout > 0) {
            logger.info("{} cache expiration timeout set: {} seconds", name,
                    expirationTimeout);
            cacheBuilder.expireAfterWrite(expirationTimeout, TimeUnit.SECONDS);
            cacheBuilder.removalListener(removalListener);
        }
        return cacheBuilder.build(cacheLoader);
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

    private Mustache locateAndParse(String templateId) {
        Reader reader = null;
        try {
            reader = locate(templateId);
            if (reader == null) {
                return null;
            }
            return parse(templateId, reader);
        } finally {
            closeReader(reader, templateId);
        }
    }

    private String locateAndRead(String templateId) {
        Reader reader = null;
        try {
            reader = locate(templateId);
            if (reader == null) {
                return null;
            }
            return CharStreams.toString(reader);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            closeReader(reader, templateId);
        }
    }

    private void closeReader(Reader reader, String templateId) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.warn("Unable to close the reader for " + templateId, e);
            }
        }
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

    private Mustache getTemplateFromCache(String templateName) {
        try {
            return templateCache.get(templateName).orNull();
        } catch (ExecutionException e) {
            throw new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                    e);
        }
    }

    private String getSourceFromCache(String templateName) {
        try {
            return sourceCache.get(templateName).orNull();
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
