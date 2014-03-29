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

import java.util.Map;

import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;

import com.google.common.collect.ImmutableMap;

/**
 * A simple builder for helpers map. It's useful when registering basic helpers
 * with sensible default names (each, if, etc.) - no need to explicitly declare
 * a helper name.
 *
 * <pre>
 * HelpersBuilder.empty().addSet().addNumberIsEven()
 *         .add(&quot;myHelperName&quot;, new MyHelper()).build();
 * </pre>
 *
 * @author Martin Kouba
 * @see MustacheEngineBuilder#registerHelpers(Map)
 */
public final class HelpersBuilder {

    public static final String EACH = "each";

    public static final String IF = "if";

    public static final String UNLESS = "unless";

    public static final String WITH = "with";

    public static final String IS = "is";

    public static final String IS_EVEN = "isEven";

    public static final String IS_ODD = "isOdd";

    public static final String SET = "set";

    public static final String INCLUDE = "include";

    private final ImmutableMap.Builder<String, Helper> builder;

    private HelpersBuilder() {
        this.builder = ImmutableMap.builder();
    }

    /**
     * @param name
     * @param helper
     * @return self
     */
    public HelpersBuilder add(String name, Helper helper) {
        builder.put(name, helper);
        return this;
    }

    /**
     * Add an {@link EachHelper} instance with the {@value #EACH} name.
     *
     * @return self
     */
    public HelpersBuilder addEach() {
        builder.put(EACH, new EachHelper());
        return this;
    }

    /**
     * Add an {@link IfHelper} instance with the {@value #IF} name.
     *
     * @return self
     */
    public HelpersBuilder addIf() {
        builder.put(IF, new IfHelper());
        return this;
    }

    /**
     * Add an {@link UnlessHelper} instance with the {@value #UNLESS} name.
     *
     * @return self
     */
    public HelpersBuilder addUnless() {
        builder.put(UNLESS, new UnlessHelper());
        return this;
    }

    /**
     * Add a {@link WithHelper} instance with the {@value #WITH} name.
     *
     * @return self
     */
    public HelpersBuilder addWith() {
        builder.put(WITH, new WithHelper());
        return this;
    }

    /**
     * Add an {@link IsHelper} instance with the {@value #IS} name.
     *
     * @return self
     */
    public HelpersBuilder addIs() {
        builder.put(IS, new IsHelper());
        return this;
    }

    /**
     * Add a {@link NumberIsEvenHelper} instance with the {@value #IS_EVEN}
     * name.
     *
     * @return self
     */
    public HelpersBuilder addIsEven() {
        builder.put(IS_EVEN, new NumberIsEvenHelper());
        return this;
    }

    /**
     * Add a {@link NumberIsOddHelper} instance with the {@value #IS_ODD} name.
     *
     * @return self
     */
    public HelpersBuilder addIsOdd() {
        builder.put(IS_ODD, new NumberIsOddHelper());
        return this;
    }

    /**
     * Add a {@link SetHelper} instance with the {@value #SET} name.
     *
     * @return self
     */
    public HelpersBuilder addSet() {
        builder.put(SET, new SetHelper());
        return this;
    }

    /**
     * Add a {@link IncludeHelper} instance with the {@value #INCLUDE} name.
     *
     * @return self
     */
    public HelpersBuilder addInclude() {
        builder.put(INCLUDE, new IncludeHelper());
        return this;
    }

    /**
     *
     * @return self
     */
    public HelpersBuilder addBuiltin() {
        addEach();
        addIf();
        addUnless();
        addWith();
        addIs();
        return this;
    }

    /**
     *
     * @return self
     */
    public HelpersBuilder addExtra() {
        addIsEven();
        addIsOdd();
        addSet();
        addInclude();
        return this;
    }

    /**
     *
     * @return the built map
     */
    public Map<String, Helper> build() {
        return builder.build();
    }

    /**
     *
     * @return a new empty builder
     */
    public static HelpersBuilder empty() {
        return new HelpersBuilder();
    }

    /**
     * Built-in helpers are registered automatically if
     * {@link EngineConfigurationKey#HANDLEBARS_SUPPORT_ENABLED} set to
     * <code>true</code>.
     *
     * @return a new builder, all built-in helpers are added automatically
     * @see HelpersBuilder#addBuiltin()
     */
    public static HelpersBuilder builtin() {
        return empty().addBuiltin();
    }

    /**
     *
     * @return a new builder, extra helpers are added automatically
     * @see HelpersBuilder#addExtra()
     */
    public static HelpersBuilder extra() {
        return empty().addExtra();
    }

    /**
     *
     * @return a new builder, all available helpers are addded automatically
     */
    public static HelpersBuilder all() {
        return builtin().addExtra();
    }

}