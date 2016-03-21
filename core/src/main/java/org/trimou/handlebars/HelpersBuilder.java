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
import org.trimou.handlebars.AlternativesHelper.MaxSelector;
import org.trimou.handlebars.AlternativesHelper.MinSelector;
import org.trimou.handlebars.AlternativesHelper.Selector;
import org.trimou.handlebars.EmbedHelper.SourceProcessor;
import org.trimou.handlebars.EvalHelper.Notation;
import org.trimou.handlebars.NumericExpressionHelper.Operator;
import org.trimou.util.ImmutableMap;
import org.trimou.util.ImmutableMap.ImmutableMapBuilder;

/**
 * A simple builder for helpers map. It's useful when registering built-in/basic
 * helpers with sensible default names (each, if, etc.) - no need to explicitly
 * declare a helper name.
 *
 * <pre>
 * HelpersBuilder.empty().addSet().addIsEven().add(&quot;myHelperName&quot;, new MyHelper())
 *         .build();
 * </pre>
 *
 * @author Martin Kouba
 * @see MustacheEngineBuilder#registerHelpers(Map)
 * @see MustacheEngineBuilder#registerHelpers(Map, boolean)
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

    public static final String EMBED = "embed";

    public static final String IS_EQUAL = "isEq";

    public static final String IS_NOT_EQUAL = "isNotEq";

    public static final String IS_NULL = "isNull";

    public static final String IS_NOT_NULL = "isNotNull";

    public static final String CHOOSE = "choose";

    public static final String WHEN = "when";

    public static final String OTHERWISE = "otherwise";

    public static final String SWITCH = "switch";

    public static final String CASE = "case";

    public static final String DEFAULT = "default";

    public static final String JOIN = "join";

    public static final String EVAL = "eval";

    public static final String NUMERIC_EXPRESSION = "numExpr";

    public static final String ASYNC = "async";

    public static final String INVOKE = "invoke";

    public static final String ALT = "alt";

    public static final String MIN = "min";

    public static final String MAX = "max";

    public static final String CACHE = "cache";

    private final ImmutableMapBuilder<String, Helper> builder;

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
     * Add an instance of {@link EachHelper} with the {@value #EACH} name.
     *
     * @return self
     */
    public HelpersBuilder addEach() {
        builder.put(EACH, new EachHelper());
        return this;
    }

    /**
     * Add an instance of {@link IfHelper} with the {@value #IF} name.
     *
     * @return self
     */
    public HelpersBuilder addIf() {
        builder.put(IF, new IfHelper());
        return this;
    }

    /**
     * Add an instance of {@link IfHelper} with the {@value #IF} name and the given else delimiters.
     *
     * @return self
     */
    public HelpersBuilder addIf(String elseStartDelimiter, String elseEndDelimiter) {
        builder.put(IF, new IfHelper(elseStartDelimiter, elseEndDelimiter));
        return this;
    }

    /**
     * Add an instance of {@link UnlessHelper} with the {@value #UNLESS} name.
     *
     * @return self
     */
    public HelpersBuilder addUnless() {
        builder.put(UNLESS, new UnlessHelper());
        return this;
    }

    /**
     * Add an instance of {@link WithHelper} with the {@value #WITH} name.
     *
     * @return self
     */
    public HelpersBuilder addWith() {
        builder.put(WITH, new WithHelper());
        return this;
    }

    /**
     * Add an instance of {@link IsHelper} with the {@value #IS} name.
     *
     * @return self
     */
    public HelpersBuilder addIs() {
        builder.put(IS, new IsHelper());
        return this;
    }

    /**
     * Add an instance of {@link NumberIsEvenHelper} with the {@value #IS_EVEN}
     * name.
     *
     * @return self
     */
    public HelpersBuilder addIsEven() {
        builder.put(IS_EVEN, new NumberIsEvenHelper());
        return this;
    }

    /**
     * Add an instance of {@link NumberIsOddHelper} with the {@value #IS_ODD}
     * name.
     *
     * @return self
     */
    public HelpersBuilder addIsOdd() {
        builder.put(IS_ODD, new NumberIsOddHelper());
        return this;
    }

    /**
     * Add an instance of {@link SetHelper} with the {@value #SET} name.
     *
     * @return self
     */
    public HelpersBuilder addSet() {
        builder.put(SET, new SetHelper());
        return this;
    }

    /**
     * Add an instance of {@link IncludeHelper} with the {@value #INCLUDE} name.
     *
     * @return self
     */
    public HelpersBuilder addInclude() {
        builder.put(INCLUDE, new IncludeHelper());
        return this;
    }

    /**
     * Add an instance of {@link EmbedHelper} with the {@value #EMBED} name.
     *
     * @return self
     */
    public HelpersBuilder addEmbed() {
        builder.put(EMBED, new EmbedHelper());
        return this;
    }

    /**
     * Add an instance of {@link EmbedHelper} with the {@value #EMBED} name and the source processor.
     *
     * @param processor
     * @return self
     */
    public HelpersBuilder addEmbed(SourceProcessor processor) {
        builder.put(EMBED, new EmbedHelper(processor));
        return this;
    }

    /**
     * Add an instance of {@link EqualsHelper} with the {@value #IS_EQUAL} name.
     *
     * @return self
     */
    public HelpersBuilder addIsEqual() {
        builder.put(IS_EQUAL, new EqualsHelper());
        return this;
    }

    /**
     * Add an instance of {@link EqualsHelper} which tests inequality with the
     * {@value #IS_NOT_EQUAL} name.
     *
     * @return self
     */
    public HelpersBuilder addIsNotEqual() {
        builder.put(IS_NOT_EQUAL, new EqualsHelper(true));
        return this;
    }

    /**
     * Add an instance of {@link NullCheckHelper} with the {@value #IS_NULL}
     * name.
     *
     * @return self
     */
    public HelpersBuilder addIsNull() {
        builder.put(IS_NULL, new NullCheckHelper());
        return this;
    }

    /**
     * Add an instance of {@link NullCheckHelper} which tests "not null" with
     * the {@value #IS_NOT_NULL} name.
     *
     * @return self
     */
    public HelpersBuilder addIsNotNull() {
        builder.put(IS_NOT_NULL, new NullCheckHelper(true));
        return this;
    }

    /**
     * Add an instance of {@link ChooseHelper} with the {@value #CHOOSE} name.
     * Also adds the dependent helpers.
     *
     * @return self
     */
    public HelpersBuilder addChoose() {
        builder.put(CHOOSE, new ChooseHelper());
        builder.put(WHEN, new ChooseHelper.WhenHelper());
        builder.put(OTHERWISE, new ChooseHelper.OtherwiseHelper());
        return this;
    }

    /**
     * Add an instance of {@link SwitchHelper} with the {@value #SWITCH} name.
     * Also adds the dependent helpers.
     *
     * @return self
     */
    public HelpersBuilder addSwitch() {
        return addSwitch(false);
    }

    /**
     * Add an instance of {@link SwitchHelper} with the {@value #SWITCH} name.
     * Also adds the dependent helpers.
     *
     * @param caseDefaultIsBreak
     *            If <code>true</code> the matching case helper terminates the flow by
     *            default.
     * @return self
     */
    public HelpersBuilder addSwitch(boolean caseDefaultIsBreak) {
        builder.put(SWITCH, new SwitchHelper());
        builder.put(CASE, new SwitchHelper.CaseHelper(caseDefaultIsBreak));
        builder.put(DEFAULT, new SwitchHelper.DefaultHelper());
        return this;
    }

    /**
     * Add an instance of {@link JoinHelper}.
     *
     * @return self
     */
    public HelpersBuilder addJoin() {
        builder.put(JOIN, new JoinHelper());
        return this;
    }

    /**
     * Add an instance of {@link EvalHelper}.
     *
     * @return self
     */
    public HelpersBuilder addEval() {
        builder.put(EVAL, new EvalHelper());
        return this;
    }

    /**
     * Add an instance of {@link EvalHelper}.
     *
     * @param notation
     * @return self
     */
    public HelpersBuilder addEval(Notation notation) {
        builder.put(EVAL, new EvalHelper(notation));
        return this;
    }

    /**
     * Add an instance of {@link NumericExpressionHelper}.
     *
     * @return self
     */
    public HelpersBuilder addNumExpr() {
        builder.put(NUMERIC_EXPRESSION, new NumericExpressionHelper());
        return this;
    }

    /**
     * Add an instance of {@link NumericExpressionHelper}.
     *
     * @param defaultOperator
     * @return self
     */
    public HelpersBuilder addNumExpr(Operator defaultOperator) {
        builder.put(NUMERIC_EXPRESSION, new NumericExpressionHelper(defaultOperator));
        return this;
    }

    /**
     * Add an instance of {@link AsyncHelper}.
     *
     * @return self
     */
    public HelpersBuilder addAsync() {
        builder.put(ASYNC, new AsyncHelper());
        return this;
    }

    /**
     * Add an instance of {@link InvokeHelper}.
     *
     * @return self
     */
    public HelpersBuilder addInvoke() {
        builder.put(INVOKE, new InvokeHelper());
        return this;
    }

    /**
     * Add an instance of {@link AlternativesHelper}.
     *
     * @return self
     */
    public HelpersBuilder addAlt() {
        builder.put(ALT, new AlternativesHelper());
        return this;
    }

    /**
     * Add an instance of {@link AlternativesHelper}.
     *
     * @param selector
     * @return self
     */
    public HelpersBuilder addAlt(Selector selector) {
        builder.put(ALT, new AlternativesHelper(selector));
        return this;
    }

    /**
     * Add an instance of {@link AlternativesHelper}.
     *
     * @return self
     * @see MinSelector
     */
    public HelpersBuilder addMin() {
        builder.put(MIN, new AlternativesHelper(new MinSelector()));
        return this;
    }

    /**
     * Add an instance of {@link AlternativesHelper}.
     *
     * @return self
     * @see MaxSelector
     */
    public HelpersBuilder addMax() {
        builder.put(MAX, new AlternativesHelper(new MaxSelector()));
        return this;
    }

    /**
     * Add an instance of {@link CacheHelper}.
     *
     * @return self
     * @see MaxSelector
     */
    public HelpersBuilder addCache() {
        builder.put(CACHE, new CacheHelper());
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
        addEmbed();
        addIsEqual();
        addIsNotEqual();
        addIsNull();
        addIsNotNull();
        addChoose();
        addSwitch();
        addJoin();
        addEval();
        addNumExpr();
        addAsync();
        addInvoke();
        addAlt();
        addMin();
        addMax();
        addCache();
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