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

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.cache.ComputingCache.Function;
import org.trimou.engine.resource.ReleaseCallback;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A simple {@link MustacheListener} collecting template rendering statistics.
 *
 * Note that the template is identified with the name/id - so data will not be
 * correct if there's more than one templates with the same name (which is
 * possible if using {@link MustacheEngine#compileMustache(String, String)}).
 *
 * This listener is not able to detect rendering errors.
 *
 * @author Martin Kouba
 */
public class SimpleStatsCollector extends AbstractStatsCollector {

    public static final String COMPUTING_CACHE_CONSUMER_ID = SimpleStatsCollector.class
            .getName();

    /**
     * Data: name -> (time -> amount)
     */
    protected ComputingCache<String, ComputingCache<Long, AtomicLong>> data;

    /**
     *
     */
    public SimpleStatsCollector() {
        this(null, null);
    }

    /**
     *
     * @param templatePredicate
     * @param timeUnit
     */
    public SimpleStatsCollector(Predicate<String> templatePredicate,
            TimeUnit timeUnit) {
        super(templatePredicate, timeUnit);
    }

    @Override
    public void init() {
        // Use computing cache because of concurrent access is required and the
        // data set is not known beforehand
        this.data = configuration.getComputingCacheFactory().create(
                COMPUTING_CACHE_CONSUMER_ID,
                new Function<String, ComputingCache<Long, AtomicLong>>() {
                    @Override
                    public ComputingCache<Long, AtomicLong> compute(String key) {
                        return configuration.getComputingCacheFactory().create(
                                COMPUTING_CACHE_CONSUMER_ID,
                                new Function<Long, AtomicLong>() {
                                    @Override
                                    public AtomicLong compute(Long key) {
                                        return new AtomicLong(0);
                                    }
                                }, null, null, null);
                    }
                }, null, null, null);
    }

    @Override
    public void renderingStarted(final MustacheRenderingEvent event) {
        if (isApplied(event.getMustacheName())) {
            final long start = System.nanoTime();
            event.registerReleaseCallback(new ReleaseCallback() {
                @Override
                public void release() {
                    data.get(event.getMustacheName())
                            .get(convert(System.nanoTime() - start))
                            .incrementAndGet();
                }
            });
        }
    }

    /**
     * Drop all the collected data.
     */
    public void clearData() {
        data.clear();
    }

    /**
     *
     * @param templateId
     * @return data for the given template
     */
    public Map<Long, Long> getData(String templateId) {
        ComputingCache<Long, AtomicLong> templateData = data
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
        for (Entry<String, ComputingCache<Long, AtomicLong>> entry : data
                .getAllPresent().entrySet()) {
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
    public SimpleStats getSimpleStats(String templateId) {
        ComputingCache<Long, AtomicLong> entry = data.getIfPresent(templateId);
        if (entry != null) {
            return new SimpleStats(templateId, entry.getAllPresent());
        }
        return null;
    }

    /**
     *
     * @return all available simple statistics
     */
    public Set<SimpleStats> getSimpleStats() {
        if (data.size() == 0) {
            return Collections.emptySet();
        }
        ImmutableSet.Builder<SimpleStats> buidler = ImmutableSet.builder();
        for (Entry<String, ComputingCache<Long, AtomicLong>> entry : data
                .getAllPresent().entrySet()) {
            buidler.add(new SimpleStats(entry.getKey(), entry.getValue()
                    .getAllPresent()));
        }
        return buidler.build();
    }

    private Map<Long, Long> getImmutableTemplateData(
            ComputingCache<Long, AtomicLong> templateData) {
        ImmutableMap.Builder<Long, Long> builder = ImmutableMap.builder();
        for (Entry<Long, AtomicLong> entry : templateData.getAllPresent()
                .entrySet()) {
            builder.put(entry.getKey(), entry.getValue().get());
        }
        return builder.build();
    }

    public class SimpleStats {

        private final String name;

        private final long executions;

        private final long totalTime;

        private final long meanTime;

        private final long minTime;

        private final long maxTime;

        SimpleStats(String name, Map<Long, AtomicLong> data) {
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

        public long getMinTime() {
            return minTime;
        }

        public long getMaxTime() {
            return maxTime;
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
            SimpleStats other = (SimpleStats) obj;
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
                    .format("SimpleStats [name: %s, executions: %s, totalTime: %s, meanTime: %s, minTime: %s, maxTime: %s]",
                            name, executions, totalTime, meanTime, minTime,
                            maxTime);
        }

    }

}