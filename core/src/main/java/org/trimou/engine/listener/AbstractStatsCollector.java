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

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.trimou.lambda.Lambda;

/**
 * It's possible to specify the time unit for measurements. By default
 * {@link TimeUnit#MILLISECONDS} is used.
 *
 * It's also possible to specify a {@link Predicate} to filter out some
 * templates. By default templates used for Lambda interpolation are skipped.
 *
 * @author Martin Kouba
 */
public class AbstractStatsCollector extends AbstractMustacheListener {

    /**
     * Skip templates used for Lambda return value interpolation.
     *
     * @see Lambda#ONEOFF_LAMBDA_TEMPLATE_PREFIX
     */
    public static final Predicate<String> IS_NOT_ONEOFF_LAMBA_TEMPLATE = (input) -> !input.startsWith(Lambda.ONEOFF_LAMBDA_TEMPLATE_PREFIX);

    private final TimeUnit timeUnit;

    private final Predicate<String> templatePredicate;

    /**
     *
     * @param templatePredicate
     * @param timeUnit
     */
    public AbstractStatsCollector(Predicate<String> templatePredicate,
            TimeUnit timeUnit) {
        this.timeUnit = timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS;
        this.templatePredicate = templatePredicate != null ? templatePredicate
                : IS_NOT_ONEOFF_LAMBA_TEMPLATE;
    }

    /**
     *
     * @return the time unit used for measurements
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public boolean isApplied(String mustacheName) {
        return templatePredicate.test(mustacheName);
    }

    /**
     *
     * @param value
     * @return the value converted from nanoseconds to the current time unit
     */
    protected long convert(long value) {
        return timeUnit.convert(value, TimeUnit.NANOSECONDS);
    }

    public static class Stats {

        private final long id;

        private final String name;

        private final long finished;

        private final long errors;

        private final long totalTime;

        private final long meanTime;

        private final long minTime;

        private final long maxTime;

        public Stats(long id, String name, long finished, long errors,
                long totalTime, long meanTime, long minTime, long maxTime) {
            this.id = id;
            this.name = name;
            this.finished = finished;
            this.errors = errors;
            this.totalTime = totalTime;
            this.meanTime = meanTime;
            this.minTime = minTime;
            this.maxTime = maxTime;
        }

        protected long getId() {
            return id;
        }

        protected String getName() {
            return name;
        }

        protected long getFinished() {
            return finished;
        }

        protected long getErrors() {
            return errors;
        }

        protected long getTotalTime() {
            return totalTime;
        }

        protected long getMeanTime() {
            return meanTime;
        }

        protected long getMinTime() {
            return minTime;
        }

        protected long getMaxTime() {
            return maxTime;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (id ^ (id >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Stats other = (Stats) obj;
            if (id != other.id)
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Stats [id=");
            builder.append(id);
            builder.append(", name=");
            builder.append(name);
            builder.append(", finished=");
            builder.append(finished);
            builder.append(", errors=");
            builder.append(errors);
            builder.append(", totalTime=");
            builder.append(totalTime);
            builder.append(", meanTime=");
            builder.append(meanTime);
            builder.append(", minTime=");
            builder.append(minTime);
            builder.append(", maxTime=");
            builder.append(maxTime);
            builder.append("]");
            return builder.toString();
        }

    }

}