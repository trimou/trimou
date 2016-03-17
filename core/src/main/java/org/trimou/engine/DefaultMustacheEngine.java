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
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.cache.ComputingCache;
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
import org.trimou.util.IOUtils;

/**
 * The default Mustache engine implementation.
 *
 * @author Martin Kouba
 */
class DefaultMustacheEngine implements MustacheEngine {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultMustacheEngine.class);

    private final ComputingCache<String, Optional<Mustache>> templateCache;

    private final ComputingCache<String, Optional<String>> sourceCache;

    private final Configuration configuration;

    private final ParserFactory parserFactory;

    private final ParsingHandlerFactory parsingHandlerFactory;

    /**
     * Workaround for CDI (JSR 299, JSR 346) - make this type proxyable so that
     * it's possible to produce an application-scoped CDI bean.
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
        templateCache.clear();
        sourceCache.clear();
    }

    private ComputingCache<String, Optional<Mustache>> buildTemplateCache() {
        return buildCache("Template",
                new ComputingCache.Function<String, Optional<Mustache>>() {
                    @Override
                    public Optional<Mustache> compute(String key) {
                        return Optional.ofNullable(locateAndParse(key));
                    }
                }, new ComputingCache.Listener<String>() {
                    @Override
                    public void entryInvalidated(String key, String cause) {
                        logger.debug(
                                "Removed template from cache [templateId: {}, cause: {}]",
                                key, cause);
                    }
                });
    }

    /**
     * Properties of the source cache are dependent on that of the template
     * cache.
     */
    private ComputingCache<String, Optional<String>> buildSourceCache() {
        return buildCache("Source",
                new ComputingCache.Function<String, Optional<String>>() {
                    @Override
                    public Optional<String> compute(String key) {
                        return Optional.ofNullable(locateAndRead(key));
                    }
                }, new ComputingCache.Listener<String>() {
                    @Override
                    public void entryInvalidated(String key, String cause) {
                        logger.debug(
                                "Removed template source from cache [templateId: {}, cause: {}]",
                                key, cause);
                    }
                });
    }

    private <K, V> ComputingCache<K, V> buildCache(String name,
            ComputingCache.Function<K, V> loader,
            ComputingCache.Listener<K> listener) {

        Long expirationTimeout = configuration
                .getLongPropertyValue(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT);

        if (expirationTimeout > 0) {
            logger.info("{} cache expiration timeout set: {} seconds", name,
                    expirationTimeout);
            expirationTimeout = expirationTimeout * 1000l;
        } else {
            expirationTimeout = null;
        }
        return configuration.getComputingCacheFactory().create(
                MustacheEngine.COMPUTING_CACHE_CONSUMER_ID, loader,
                expirationTimeout, null, listener);
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
            return IOUtils.toString(reader);
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
            return templateCache.get(templateName).orElse(null);
        } catch (Exception e) {
            throw unwrapUncheckedExecutionException(e);
        }
    }

    private String getSourceFromCache(String templateName) {
        try {
            return sourceCache.get(templateName).orElse(null);
        } catch (Exception e) {
            throw unwrapUncheckedExecutionException(e);
        }
    }

    private RuntimeException unwrapUncheckedExecutionException(Exception e) {
        if (e.getCause() instanceof RuntimeException) {
            return (RuntimeException) e.getCause();
        }
        return new MustacheException(MustacheProblem.TEMPLATE_LOADING_ERROR,
                e.getCause());
    }

    /**
     *
     * @author Martin Kouba
     */
    private static class DefaultMustacheCompilationEvent implements
            MustacheCompilationEvent {

        private final Mustache mustache;

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
