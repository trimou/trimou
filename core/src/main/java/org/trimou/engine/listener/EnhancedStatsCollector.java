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

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.trimou.Mustache;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.util.Checker;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableSet;
import org.trimou.util.ImmutableSet.ImmutableSetBuilder;

/**
 * Unlike {@link SimpleStatsCollector} this listener is able to detect rendering
 * errors. Also {@link Mustache#getGeneratedId()} is used to map statistics to a
 * template. On the other hand it's more resource-intensive.
 *
 * @author Martin Kouba
 */
public class EnhancedStatsCollector extends AbstractStatsCollector {

    public static final String COMPUTING_CACHE_CONSUMER_ID = EnhancedStatsCollector.class
            .getName();

    private final ConcurrentMap<Long, String> idsToNames;

    protected ComputingCache<Long, ConcurrentMap<Long, ExecutionData>> data;

    public EnhancedStatsCollector() {
        this(null, null);
    }

    public EnhancedStatsCollector(Predicate<String> templatePredicate,
            TimeUnit timeUnit) {
        super(templatePredicate, timeUnit);
        idsToNames = new ConcurrentHashMap<>();
    }

    @Override
    public void renderingStarted(MustacheRenderingEvent event) {
        if (isApplied(event.getMustacheName())) {
            idsToNames.putIfAbsent(event.getMustacheGeneratedId(),
                    event.getMustacheName());
            data.get(event.getMustacheGeneratedId()).put(
                    event.getGeneratedId(), new ExecutionData(System.nanoTime()));
        }
    }

    @Override
    public void renderingFinished(MustacheRenderingEvent event) {
        if (isApplied(event.getMustacheName())) {
            data.get(event.getMustacheGeneratedId())
                    .get(event.getGeneratedId()).setEnd(System.nanoTime());
        }
    }

    @Override
    protected void init() {
        data = configuration.getComputingCacheFactory().create(COMPUTING_CACHE_CONSUMER_ID, key ->
                new ConcurrentHashMap<>(), null, null, null);
    }

    /**
     *
     * @param mustache
     * @return the statistics for the given template
     */
    public Stats getStats(Mustache mustache) {
        Checker.checkArgumentNotNull(mustache);
        ConcurrentMap<Long, ExecutionData> times = data.getIfPresent(mustache
                .getGeneratedId());
        if (times != null) {
            return parseData(mustache.getName(), mustache.getGeneratedId(),
                    times.values());
        }
        return null;
    }

    /**
     *
     * @return the statistics
     */
    public Set<Stats> getStats() {
        ImmutableSetBuilder<Stats> builder = ImmutableSet.builder();
        for (Entry<Long, ConcurrentMap<Long, ExecutionData>> entry : data
                .getAllPresent().entrySet()) {
            builder.add(parseData(idsToNames.get(entry.getKey()),
                    entry.getKey(), entry.getValue().values()));
        }
        return builder.build();
    }

    /**
     *
     * @param mustache
     * @return the raw data for the given template
     */
    public Collection<ExecutionData> getRawData(Mustache mustache) {
        Checker.checkArgumentNotNull(mustache);
        ConcurrentMap<Long, ExecutionData> executions = data.getIfPresent(mustache
                .getGeneratedId());
        if (executions != null) {
            return ImmutableMap.copyOf(executions).values();
        }
        return null;
    }


    /**
     * Drop all the collected data.
     */
    public void clearData() {
        data.clear();
    }

    private Stats parseData(String mustacheName, long mustacheId,
            Collection<ExecutionData> executions) {
        long errors = 0L;
        long finished = 0L;
        long totalTime = 0L;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0L;
        for (ExecutionData execution : executions) {
            if (execution.isFinished()) {
                long value = convert(execution.getValue());
                finished += 1;
                totalTime += value;
                if (value > maxTime) {
                    maxTime = value;
                }
                if (value < minTime) {
                    minTime = value;
                }
            } else {
                errors += 1;
            }
        }
        long meanTime = finished > 0 ? (totalTime / finished) : 0;
        return new Stats(mustacheId, mustacheName, finished, errors, totalTime,
                meanTime, minTime, maxTime);
    }

    public static class ExecutionData {

        private final long start;

        private Long end;

        ExecutionData(long start) {
            this.start = start;
        }

        void setEnd(long end) {
            this.end = end;
        }

        boolean isFinished() {
            return end != null;
        }

        long getValue() {
            return end - start;
        }

    }

}