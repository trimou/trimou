/*
 * Copyright 2016 Martin Kouba
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

import static org.trimou.handlebars.OptionsHashKeys.EXPIRE;
import static org.trimou.handlebars.OptionsHashKeys.GUARD;
import static org.trimou.handlebars.OptionsHashKeys.KEY;
import static org.trimou.handlebars.OptionsHashKeys.UNIT;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.cache.ComputingCache;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.HelperDefinition.ValuePlaceholder;
import org.trimou.util.ImmutableSet;

/**
 * Allows to cache template fragments in memory. This might be useful for
 * resource-intensive parts of the template that rarely change (e.g. a user menu
 * in a webapp).
 *
 * <code>
 * {{#cache}}
 *   All the content will be cached!
 * {{/cache}}
 * </code>
 *
 * <p>
 * A <code>key</code> might be used to cache multiple versions of the fragment
 * for the same part of the template, i.e. each version depends on some context
 * (e.g. logged-in user):
 * </p>
 *
 * <code>
 * {{#cache key=username}}
 *   All the content will be cached!
 * {{/cache}}
 * </code>
 *
 * <p>
 * The cached fragments can be automatically updated. Either set the expiration
 * or the guard. <code>expire</code> value must be a Long value.
 * <code>unit</code> must be a {@link TimeUnit} constant or its
 * {@link Object#toString()} ( {@link TimeUnit#MILLISECONDS} is the default).
 * <code>guard</code> may be any object - every time the helper is executed the
 * current value of the {@link Object#toString()} is compared to the
 * {@link Object#toString()} value of the guard referenced during the last
 * update of the fragment. If they're not equal the fragment is updated.
 * </p>
 *
 * <code>
 * {{#cache key=username expire=2 unit="HOURS" guard=roles}}
 *   All the content will be cached!
 * {{/cache}}
 * </code>
 *
 * <p>
 * It's also possible to invalidate the cache manually (e.g. after user logout).
 * See also {@link #invalidateFragments()} and
 * {@link #invalidateFragments(String)}.
 * </p>
 *
 * <p>
 * To limit the size of the fragment cache use
 * {@link #FRAGMENT_CACHE_MAX_SIZE_KEY}.
 * </p>
 *
 * @author Martin Kouba
 */
public class CacheHelper extends BasicSectionHelper {

    /**
     * Limit the size of the fragment cache.
     */
    public static final ConfigurationKey FRAGMENT_CACHE_MAX_SIZE_KEY = new SimpleConfigurationKey(
            CacheHelper.class.getName() + ".fragmentCacheMaxSize", 500L);

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CacheHelper.class);

    private volatile ComputingCache<Key, Fragment> fragments;

    @Override
    public void execute(Options options) {

        Map<String, Object> hash = options.getHash();
        Object key = hash.get(KEY);
        Object guard = hash.get(GUARD);
        Object expire = hash.get(EXPIRE);
        Object unit = expire != null ? hash.get(UNIT) : null;

        StringBuilder fragmentKey = new StringBuilder();
        fragmentKey.append(options.getTagInfo().getTemplateGeneratedId());
        fragmentKey.append(options.getTagInfo().getId());
        if (key != null) {
            fragmentKey.append(key.toString());
        }

        Fragment fragment = fragments
                .get(new Key(fragmentKey.toString(), options));

        if (fragment.getHits() > 0 && (isExpired(fragment, expire, unit)
                || isGuardCompromised(fragment, guard))) {
            fragment.update(getContent(options), guard);
        }
        fragment.touch();
        options.append(fragment.getContent());
    }

    public void init() {
        super.init();
        this.fragments = configuration.getComputingCacheFactory().create(
                CacheHelper.class.getName(),
                key -> {
                    Fragment fragment = new Fragment();
                    fragment.update(getContent(key.getOptions()),
                            key.getOptions().getHash().get(GUARD));
                    key.cleanupAfterCompute();
                    return fragment;
                }, null,
                configuration.getLongPropertyValue(FRAGMENT_CACHE_MAX_SIZE_KEY),
                null);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(FRAGMENT_CACHE_MAX_SIZE_KEY);
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

    @Override
    public void validate(HelperDefinition definition) {
        super.validate(definition);
        Map<String, Object> hash = definition.getHash();
        if (hash.containsKey(EXPIRE)) {
            Object expire = hash.get(EXPIRE);
            if (!(expire instanceof ValuePlaceholder)) {
                try {
                    Long.valueOf(expire.toString());
                } catch (NumberFormatException e) {
                    throw new MustacheException(
                            MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                            e);
                }
            }
            Object unit = hash.get(UNIT);
            if (unit != null && !(unit instanceof TimeUnit)
                    && !(unit instanceof ValuePlaceholder)) {
                try {
                    TimeUnit.valueOf(unit.toString());
                } catch (Exception e) {
                    throw new MustacheException(
                            MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                            e);
                }
            }
        }
        if (!hash.containsKey(EXPIRE) && !hash.containsKey(GUARD)) {
            LOGGER.info(
                    "Cache fragment will not be automatically updated [helper: {}, template: {}, line: {}]",
                    getClass().getName(),
                    definition.getTagInfo().getTemplateName(),
                    definition.getTagInfo().getLine());
        }
    }

    /**
     * Invalidate all the cache fragments.
     */
    public void invalidateFragments() {
        if (fragments == null) {
            return;
        }
        fragments.clear();
    }

    /**
     * Invalidate the cache fragments whose key contains the given part of the
     * key.
     *
     * @param keyPart
     */
    public void invalidateFragments(final String keyPart) {
        if (fragments == null || keyPart == null) {
            return;
        }
        fragments.invalidate(fragmentKey -> fragmentKey.getKey().contains(keyPart));
    }

    @Override
    protected Set<String> getSupportedHashKeys() {
        return ImmutableSet.of(KEY, GUARD, EXPIRE, UNIT);
    }

    private boolean isExpired(Fragment fragment, Object expire, Object unit) {
        if (expire == null) {
            return false;
        }
        TimeUnit timeUnit;
        if (unit != null) {
            if (unit instanceof TimeUnit) {
                timeUnit = (TimeUnit) unit;
            } else {
                try {
                    timeUnit = TimeUnit.valueOf(unit.toString());
                } catch (Exception e) {
                    throw new MustacheException(
                            MustacheProblem.RENDER_HELPER_INVALID_OPTIONS, e);
                }
            }
        } else {
            timeUnit = TimeUnit.MILLISECONDS;
        }
        Long duration;
        if (expire instanceof Long) {
            duration = (Long) expire;
        } else if (expire instanceof Integer) {
            duration = ((Integer) expire).longValue();
        } else {
            try {
                duration = Long.valueOf(expire.toString());
            } catch (Exception e) {
                throw new MustacheException(
                        MustacheProblem.RENDER_HELPER_INVALID_OPTIONS, e);
            }
        }
        return (System.currentTimeMillis() - fragment.getLastUsed()) > timeUnit
                .toMillis(duration);
    }

    private boolean isGuardCompromised(Fragment fragment, Object guard) {
        if (guard == null) {
            return false;
        }
        return !guard.toString().equals(fragment.getGuard());
    }

    private static String getContent(Options options) {
        StringBuilder content = new StringBuilder();
        options.fn(content);
        return content.toString();
    }

    private static class Fragment {

        private final AtomicLong hits;

        private final AtomicLong lastUsed;

        private final AtomicReference<String> content;

        private final AtomicReference<String> guard;

        private Fragment() {
            this.hits = new AtomicLong(0);
            this.lastUsed = new AtomicLong();
            this.content = new AtomicReference<>();
            this.guard = new AtomicReference<>();
        }

        /**
         * @return the cached content
         */
        String getContent() {
            return content.get();
        }

        void touch() {
            this.hits.incrementAndGet();
            this.lastUsed.set(System.currentTimeMillis());
        }

        void update(String content, Object guard) {
            this.content.set(content);
            if (guard != null) {
                this.guard.set(guard.toString());
            }
        }

        /**
         * @return the lastHit
         */
        Long getLastUsed() {
            return lastUsed.get();
        }

        /**
         * @return the number of hits
         */
        Long getHits() {
            return hits.get();
        }

        /**
         * @return the guard
         */
        String getGuard() {
            return guard.get();
        }

    }

    private static class Key {

        private final String key;

        private Options options;

        private Key(String key, Options options) {
            this.key = key;
            this.options = options;
        }

        void cleanupAfterCompute() {
            this.options = null;
        }

        /**
         * @return the key
         */
        String getKey() {
            return key;
        }

        /**
         * @return the options
         */
        Options getOptions() {
            return options;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
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
            Key other = (Key) obj;
            if (key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!key.equals(other.key)) {
                return false;
            }
            return true;
        }

    }

}
