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
package org.trimou.prettytime.resolver;

import static org.trimou.engine.priority.Priorities.rightAfter;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.resolver.ArrayIndexResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.TransformResolver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * PrettyTime resolver.
 *
 * @author Martin Kouba
 */
public class PrettyTimeResolver extends TransformResolver {

    private static final Logger logger = LoggerFactory
            .getLogger(PrettyTimeResolver.class);

    public static final int PRETTY_TIME_RESOLVER_PRIORITY = rightAfter(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY);

    public static final ConfigurationKey MATCH_NAME_KEY = new SimpleConfigurationKey(
            PrettyTimeResolver.class.getName() + ".matchName", "prettyTime");

    /**
     * Lazy loading cache of PrettyTime instances
     */
    private LoadingCache<Locale, PrettyTime> prettyTimeCache;

    /**
     *
     */
    public PrettyTimeResolver() {
        this(PRETTY_TIME_RESOLVER_PRIORITY);
    }

    /**
     *
     * @param priority
     */
    public PrettyTimeResolver(int priority) {
        super(priority);
    }

    @Override
    public Object transform(Object contextObject, String name,
            ResolutionContext context) {

        Date formattableObject = getFormattableObject(contextObject);

        if (formattableObject == null) {
            return null;
        }
        return prettyTimeCache.getUnchecked(getCurrentLocale()).format(
                formattableObject);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(MATCH_NAME_KEY);
    }

    @Override
    public void init(Configuration configuration) {
        super.init(configuration);

        setMatchingNames(configuration.getStringPropertyValue(MATCH_NAME_KEY));
        prettyTimeCache = CacheBuilder.newBuilder().maximumSize(10)
                .build(new CacheLoader<Locale, PrettyTime>() {

                    @Override
                    public PrettyTime load(Locale locale) throws Exception {
                        return new PrettyTime(locale);
                    }
                });
        logger.info("Initialized [matchingName: {}]", matchingName(0));
    }

    private Date getFormattableObject(Object contextObject) {
        if (contextObject instanceof Date) {
            return (Date) contextObject;
        } else if (contextObject instanceof Calendar) {
            return ((Calendar) contextObject).getTime();
        } else if (contextObject instanceof Long) {
            return new Date((Long) contextObject);
        }
        return null;
    }

}
