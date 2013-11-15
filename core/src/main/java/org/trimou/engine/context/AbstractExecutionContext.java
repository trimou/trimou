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
package org.trimou.engine.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.engine.segment.TemplateSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;

/**
 * Abstract execution context.
 *
 * @author Martin Kouba
 */
abstract class AbstractExecutionContext implements ExecutionContext {

    /**
     * Immutable engine configuration
     */
    protected final Configuration configuration;

    /**
     * LIFO stack of context objects
     */
    protected final Deque<Object> contextObjectStack;

    /**
     * LIFO stack of template invocations
     */
    protected final Deque<TemplateSegment> templateInvocationStack;

    /**
     * Lazily initialized map of defining/overriding sections
     */
    protected Map<String, ExtendSectionSegment> definingSections = null;

    /**
     * @see EngineConfigurationKey#TEMPLATE_RECURSIVE_INVOCATION_LIMIT
     */
    private final int templateRecursiveInvocationLimit;

    private final Resolver[] resolvers;

    /**
     *
     * @param configuration
     */
    protected AbstractExecutionContext(Configuration configuration) {
        this.configuration = configuration;
        this.resolvers = configuration.getResolvers().toArray(new Resolver[configuration.getResolvers().size()]);

        this.contextObjectStack =  new ArrayDeque<Object>();
        this.templateInvocationStack = new ArrayDeque<TemplateSegment>();

        this.templateRecursiveInvocationLimit = configuration
                .getIntegerPropertyValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT);
    }

    @Override
    public void push(TargetStack stack, Object object) {
        Checker.checkArgumentNotNull(stack);
        Checker.checkArgumentNotNull(object);

        switch (stack) {
        case CONTEXT:
            contextObjectStack.addFirst(object);
            break;
        case TEMPLATE_INVOCATION:
            pushTemplateInvocation((TemplateSegment) object);
            break;
        default:
            throw new IllegalStateException("Invalid stack type");
        }
    }

    @Override
    public Object pop(TargetStack stack) {
        Checker.checkArgumentNotNull(stack);
        switch (stack) {
        case CONTEXT:
            return contextObjectStack.removeFirst();
        case TEMPLATE_INVOCATION:
            return templateInvocationStack.removeFirst();
        default:
            throw new IllegalStateException("Invalid stack type");
        }
    }

    @Override
    public void addDefiningSection(String name, ExtendSectionSegment segment) {
        if (definingSections == null) {
            // Lazy init - ok, context is not thread-safe
            definingSections = new HashMap<String, ExtendSectionSegment>();
        }
        if (!definingSections.containsKey(name)) {
            definingSections.put(name, segment);
        }
    }

    @Override
    public ExtendSectionSegment getDefiningSection(String name) {
        if (definingSections == null || definingSections.isEmpty()) {
            return null;
        }
        return definingSections.get(name);
    }

    /**
     * Resolve the leading context object (the first part of the key). E.g.
     * <code>foo</code> in <code>{{foo.bar.name}}</code> may identify a property
     * of some context object on the stack (passed data, section iteration,
     * nested context, ...), or some context and data unrelated object (e.g. CDI
     * bean).
     *
     * @param name
     * @return the resolved leading context object
     */
    protected Object resolveLeadingContextObject(String name,
            ResolutionContext context) {

        Object leading = null;

        for (Object contextObject : contextObjectStack) {

            leading = resolve(contextObject, name, context);

            if (leading != null) {
                // Skip following
                break;
            }
        }
        if (leading == null) {
            // Try to resolve context unrelated objects
            leading = resolve(null, name, context);
        }
        return leading;
    }

    /**
     *
     * @param contextObject
     * @param name
     * @param context
     * @return the resolved object
     */
    protected Object resolve(Object contextObject, String name,
            ResolutionContext context) {
        Object value = null;
        for (int i = 0; i < resolvers.length; i++) {
            value = resolvers[i].resolve(contextObject, name, context);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    private void pushTemplateInvocation(TemplateSegment template) {

        Checker.checkArgumentNotNull(template);

        if (getTemplateInvocations(template) > templateRecursiveInvocationLimit) {
            throw new MustacheException(
                    MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED,
                    "Recursive invocation limit exceeded [limit: %s, stack: %s]",
                    templateRecursiveInvocationLimit, templateInvocationStack);
        }
        templateInvocationStack.addFirst(template);
    }

    private int getTemplateInvocations(TemplateSegment template) {
        int invocations = 0;
        for (TemplateSegment segment : templateInvocationStack) {
            if (segment.equals(template)) {
                invocations++;
            }
        }
        return invocations;
    }

}
