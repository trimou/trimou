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
package org.trimou.engine.listener;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.lambda.Lambda;
import org.trimou.util.Checker;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A simple {@link MustacheListener} collecting template rendering statistics.
 * Note that the template is identified with the name/id - so data will not be
 * correct if there's more than one templates with the same name.
 *
 * It's possible to specify the time unit for measurements. By default
 * {@link TimeUnit#MILLISECONDS} is used.
 *
 * It's also possible to specify a {@link Predicate} to filter out some
 * templates. By default templates used for Lambda interpolation are skipped.
 *
 * @author Martin Kouba
 */
public class SimpleStatisticsCollector extends AbstractMustacheListener {

    /**
     * Skip templates used for Lambda return value interpolation.
     *
     * @see Lambda#ONEOFF_LAMBDA_TEMPLATE_PREFIX
     */
    public static final Predicate<String> SKIP_ONEOFF_LAMBA_TEMPLATES = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return !input.startsWith(Lambda.ONEOFF_LAMBDA_TEMPLATE_PREFIX);
        }
    };

    protected final TimeUnit timeUnit;

    protected final Predicate<String> templateMatcher;

    /**
     * Data: name -> (time -> amount)
     */
    protected final LoadingCache<String, LoadingCache<Long, AtomicLong>> data;

    /**
     *
     */
    public SimpleStatisticsCollector() {
        this(SKIP_ONEOFF_LAMBA_TEMPLATES, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param templateMatcher
     * @param timeUnit
     */
    public SimpleStatisticsCollector(Predicate<String> templateMatcher,
            TimeUnit timeUnit) {
        Checker.checkArgumentsNotNull(templateMatcher, timeUnit);
        this.timeUnit = timeUnit;
        this.templateMatcher = templateMatcher;
        // Use LoadingCache because of concurrent access is required and the
        // data set is not known beforehand
        this.data = CacheBuilder.newBuilder().build(
                new CacheLoader<String, LoadingCache<Long, AtomicLong>>() {
                    @Override
                    public LoadingCache<Long, AtomicLong> load(String key)
                            throws Exception {
                        return CacheBuilder.newBuilder().build(
                                new CacheLoader<Long, AtomicLong>() {
                                    @Override
                                    public AtomicLong load(Long key)
                                            throws Exception {
                                        return new AtomicLong(0);
                                    }
                                });
                    }
                });
    }

    @Override
    public void renderingStarted(final MustacheRenderingEvent event) {
        if (templateMatcher.apply(event.getMustacheName())) {
            final long start = System.nanoTime();
            event.registerReleaseCallback(new ReleaseCallback() {
                @Override
                public void release() {
                    data.getUnchecked(event.getMustacheName())
                            .getUnchecked(
                                    timeUnit.convert(System.nanoTime() - start,
                                            TimeUnit.NANOSECONDS))
                            .incrementAndGet();
                }
            });
        }
    }

    /**
     * Drop all the collected data.
     */
    public void clearData() {
        data.invalidateAll();
        data.cleanUp();
    }

    /**
     *
     * @param templateId
     * @return data for the given template
     */
    public Map<Long, Long> getData(String templateId) {
        LoadingCache<Long, AtomicLong> templateData = data
                .getIfPresent(templateId);
        if (templateData != null) {
            return getImmutableTemplateData(templateData);
        }
        return null;
    }

    /**
     *
     * @return all collected data
     */
    public Map<String, Map<Long, Long>> getData() {
        if (data.size() == 0) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<String, Map<Long, Long>> builder = ImmutableMap
                .builder();
        for (Entry<String, LoadingCache<Long, AtomicLong>> entry : data.asMap()
                .entrySet()) {
            builder.put(entry.getKey(),
                    getImmutableTemplateData(entry.getValue()));
        }
        return builder.build();
    }

    /**
     *
     * @param templateId
     * @return a simple statistics for the given template
     */
    public SimpleTemplateStatistics getSimpleStatistics(String templateId) {
        LoadingCache<Long, AtomicLong> entry = data.getIfPresent(templateId);
        if (entry != null) {
            return new SimpleTemplateStatistics(templateId, entry.asMap());
        }
        return null;
    }

    /**
     *
     * @return all available simple statistics
     */
    public Set<SimpleTemplateStatistics> getSimpleStatistics() {
        if (data.size() == 0) {
            return Collections.emptySet();
        }
        ImmutableSet.Builder<SimpleTemplateStatistics> buidler = ImmutableSet
                .builder();
        for (Entry<String, LoadingCache<Long, AtomicLong>> entry : data.asMap()
                .entrySet()) {
            buidler.add(new SimpleTemplateStatistics(entry.getKey(), entry
                    .getValue().asMap()));
        }
        return buidler.build();
    }

    /**
     *
     * @return
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    private Map<Long, Long> getImmutableTemplateData(
            LoadingCache<Long, AtomicLong> templateData) {
        ImmutableMap.Builder<Long, Long> builder = ImmutableMap.builder();
        for (Entry<Long, AtomicLong> entry : templateData.asMap().entrySet()) {
            builder.put(entry.getKey(), entry.getValue().get());
        }
        return builder.build();
    }

    public class SimpleTemplateStatistics {

        private final String name;

        private final long executions;

        private final long totalTime;

        private final long meanTime;

        private final long minTime;

        private final long maxTime;

        SimpleTemplateStatistics(String name, Map<Long, AtomicLong> data) {
            this.name = name;
            long executions = 0l;
            long totalTime = 0l;
            for (Entry<Long, AtomicLong> entry : data.entrySet()) {
                executions += entry.getValue().get();
                totalTime += entry.getKey() * entry.getValue().get();
            }
            this.executions = executions;
            this.totalTime = totalTime;
            this.meanTime = (totalTime / executions);
            this.minTime = Collections.min(data.keySet());
            this.maxTime = Collections.max(data.keySet());
        }

        public String getName() {
            return name;
        }

        public long getExecutions() {
            return executions;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public long getMeanTime() {
            return meanTime;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SimpleTemplateStatistics other = (SimpleTemplateStatistics) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return String
                    .format("SimpleTemplateStatistics [name: %s, executions: %s, totalTime: %s, meanTime: %s, minTime: %s, maxTime: %s, timeUnit: %s]",
                            name, executions, totalTime, meanTime, minTime,
                            maxTime, timeUnit);
        }

    }

}