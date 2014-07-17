/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.handlebars;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.util.Arrays;
import org.trimou.util.Checker;

/**
 * <p>
 * A simple log helper. There is a special {@link Builder} for convenience.
 * </p>
 *
 * <p>
 * First register the helper instance:
 * </p>
 * <code>
 * MustacheEngineBuilder.newBuilder().registerHelper("log", LogHelper.builder().setDefaultLevel(Level.DEBUG).build()).build();
 * </code>
 *
 * <p>
 * Than use the helper in the template:
 * </p>
 * <code>
 * {{log "Hello"}}
 * </code>
 *
 * <p>
 * The message need not be a string literal:
 * </p>
 * <code>
 * {{log foo.message}}
 * </code>
 *
 * <p>
 * You may also override the default log level:
 * </p>
 * <code>
 * {{log "" level="DEBUG"}}
 * </code>
 *
 * <p>
 * And also use message parameters:
 * </p>
 * <code>
 * {{log "Number of items found: {}" items.size}}
 * </code>
 * <p>
 * Not that the final output will depend on SLF4J configuration.
 * </p>
 *
 * @author Martin Kouba
 * @see Level
 * @see LoggerAdapter
 */
public class LogHelper extends BasicValueHelper {

    private static final Logger logger = LoggerFactory
            .getLogger(LogHelper.class);

    private static final String OPTION_KEY_LEVEL = "level";

    private final LoggerAdapter adapter;

    private final Level defaultLevel;

    private final boolean appendTemplateInfo;

    /**
     *
     * @param adapter
     * @param defaultLevel
     * @param appendTemplateInfo
     *            If true, a template name and helper line will be appended to
     *            each log message
     */
    public LogHelper(LoggerAdapter adapter, Level defaultLevel,
            boolean appendTemplateInfo) {
        Checker.checkArgumentsNotNull(adapter, defaultLevel);
        this.defaultLevel = defaultLevel;
        this.adapter = adapter;
        this.appendTemplateInfo = appendTemplateInfo;
    }

    @Override
    public void execute(Options options) {
        String message = options.getParameters().get(0).toString();
        if (appendTemplateInfo) {
            StringBuilder builder = new StringBuilder(message);
            builder.append(" [");
            builder.append(options.getTagInfo().getTemplateName());
            builder.append(":");
            builder.append(options.getTagInfo().getLine());
            builder.append("]");
            message = builder.toString();
        }
        adapter.log(getLevel(options.getHash()), message,
                getMessageParams(options.getParameters()));
    }

    private Level getLevel(Map<String, Object> hash) {
        if (hash.isEmpty() || !hash.containsKey(OPTION_KEY_LEVEL)) {
            return defaultLevel;
        }
        String customLevel = hash.get(OPTION_KEY_LEVEL).toString();
        Level level = Level.parse(customLevel);
        if (level == null) {
            logger.warn(
                    "Unsupported level specified: {}, using the default one: {}",
                    customLevel, defaultLevel);
            level = defaultLevel;
        }
        return level;
    }

    private Object[] getMessageParams(List<Object> params) {
        if (params.size() > 1) {
            return params.subList(1, params.size()).toArray();
        }
        return Arrays.EMPTY_OBJECT_ARRAY;
    }

    /**
     *
     * @return a new instance of builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Level level;

        private LoggerAdapter adapter;

        private boolean appendTemplateInfo = true;

        /**
         * If not set, {@link Level#INFO} is used.
         *
         * @param level
         * @return builder
         */
        public Builder setDefaultLevel(Level level) {
            this.level = level;
            return this;
        }

        /**
         * If not set, a default adapter is used.
         *
         * @param adapter
         * @return builder
         */
        public Builder setLoggerAdapter(LoggerAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        /**
         * If true, a template name and helper line will be appended to each log
         * message.
         *
         * @param value
         * @return builder
         */
        public Builder setAppendTemplateInfo(boolean value) {
            this.appendTemplateInfo = value;
            return this;
        }

        public LogHelper build() {
            return new LogHelper(adapter != null ? adapter
                    : new Slf4jLoggerAdapter(LogHelper.class.getName()),
                    level != null ? level : Level.INFO, appendTemplateInfo);
        }

    }

    /**
     * Log level.
     */
    public static enum Level {

        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE;

        static Level parse(String value) {
            for (Level level : values()) {
                if (value.equals(level.toString())) {
                    return level;
                }
            }
            return null;
        }

    }

    /**
     * Log event adapter.
     */
    public static interface LoggerAdapter {

        /**
         *
         * @param level
         * @param message
         * @param params
         */
        void log(Level level, String message, Object[] params);

    }

    /**
     * A default adapter implementation for SLF4J.
     */
    public static class Slf4jLoggerAdapter implements LoggerAdapter {

        private final Logger logger;

        public Slf4jLoggerAdapter(String name) {
            this.logger = LoggerFactory.getLogger(name);
        }

        @Override
        public void log(Level level, String message, Object[] params) {
            switch (level) {
            case ERROR:
                logger.error(message, params);
                break;
            case WARN:
                logger.warn(message, params);
                break;
            case INFO:
                logger.info(message, params);
                break;
            case DEBUG:
                logger.debug(message, params);
                break;
            case TRACE:
                logger.trace(message, params);
                break;
            default:
                break;
            }
        }
    }
}
