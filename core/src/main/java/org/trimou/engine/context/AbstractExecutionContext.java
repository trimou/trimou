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
import java.util.concurrent.atomic.AtomicReference;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.parser.Template;
import org.trimou.engine.resolver.EnhancedResolver;
import org.trimou.engine.resolver.EnhancedResolver.Hint;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.segment.ExtendSectionSegment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.util.Checker;

/**
 * Abstract execution context.
 *
 * @author Martin Kouba
 */
abstract class AbstractExecutionContext implements ExecutionContext {

    private static final String INVALID_STACK_TYPE = "Invalid stack type";

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
    protected final Deque<Template> templateInvocationStack;

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
     * Allows to skip iteration if a hint is provided
     */
    private Object lastContextObject;

    /**
     *
     * @param configuration
     */
    protected AbstractExecutionContext(Configuration configuration) {
        this.configuration = configuration;
        this.resolvers = configuration.getResolvers().toArray(
                new Resolver[configuration.getResolvers().size()]);
        this.contextObjectStack = new ArrayDeque<Object>();
        this.templateInvocationStack = new ArrayDeque<Template>();
        this.templateRecursiveInvocationLimit = configuration
                .getIntegerPropertyValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT);
        this.lastContextObject = null;
    }

    @Override
    public void push(TargetStack stack, Object object) {
        Checker.checkArgumentNotNull(stack);
        Checker.checkArgumentNotNull(object);

        switch (stack) {
        case CONTEXT:
            lastContextObject = object;
            contextObjectStack.addFirst(object);
            break;
        case TEMPLATE_INVOCATION:
            pushTemplateInvocation((Template) object);
            break;
        default:
            throw new IllegalStateException(INVALID_STACK_TYPE);
        }
    }

    @Override
    public Object pop(TargetStack stack) {
        Checker.checkArgumentNotNull(stack);
        switch (stack) {
        case CONTEXT:
            Object element = contextObjectStack.removeFirst();
            lastContextObject = contextObjectStack.peekFirst();
            return element;
        case TEMPLATE_INVOCATION:
            return templateInvocationStack.removeFirst();
        default:
            throw new IllegalStateException(INVALID_STACK_TYPE);
        }
    }

    @Override
    public Object peek(TargetStack stack) {
        Checker.checkArgumentNotNull(stack);
        switch (stack) {
        case CONTEXT:
            return contextObjectStack.peekFirst();
        case TEMPLATE_INVOCATION:
            return templateInvocationStack.peekFirst();
        default:
            throw new IllegalStateException(INVALID_STACK_TYPE);
        }
    }

    @Override
    public void addDefiningSection(String name, ExtendSectionSegment segment) {
        if (definingSections == null) {
            // Lazy init - ok, context is used in a single thread
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

    @Override
    public void clearDefiningSections() {
        if (definingSections != null) {
            definingSections.clear();
        }
    }

    /**
     * Resolve the leading context object (the first part of the key). E.g.
     * <code>foo</code> in <code>{{foo.bar.name}}</code> may identify a property
     * of some context object on the stack (passed data, section iteration,
     * nested context, ...), or some context and data unrelated object (e.g. CDI
     * bean).
     *
     * @param name
     * @param value
     *            The value wrapper - ResolutionContext
     * @param hintRef
     * @return the resolved leading context object
     * @see Hint
     */
    protected Object resolveLeadingContextObject(String name,
            ValueWrapper value, AtomicReference<Hint> hintRef) {

        Object leading = null;

        if (hintRef != null) {
            Hint hint = hintRef.get();
            if (hint != null && lastContextObject != null) {
                leading = hint.resolve(lastContextObject, name);
            }
        }

        if (leading == null) {
            for (Object contextObject : contextObjectStack) {
                leading = resolve(contextObject, name, value, hintRef != null);
                if (leading != null) {
                    // Leading context object found
                    break;
                }
            }
            if (leading == null) {
                // Leading context object not found - try to resolve context
                // unrelated objects (JNDI lookup, CDI, etc.)
                leading = resolve(null, name, value, hintRef != null);
            }
        }
        return leading;
    }

    /**
     *
     * @param contextObject
     * @param name
     * @param value
     *            The value wrapper - ResolutionContext
     * @param initHint
     * @return the resolved object
     */
    protected Object resolve(Object contextObject, String name,
            ValueWrapper value, boolean initHint) {
        Object resolved = null;
        for (int i = 0; i < resolvers.length; i++) {
            resolved = resolvers[i].resolve(contextObject, name, value);
            if (resolved != null) {
                if (initHint) {
                    // Initialize a new hint if possible
                    Resolver resolver = resolvers[i];
                    if (resolver instanceof EnhancedResolver) {
                        value.setHint(((EnhancedResolver) resolver).createHint(
                                contextObject, name));
                    }
                }
                break;
            }
        }
        return resolved;
    }

    private void pushTemplateInvocation(Template template) {

        Checker.checkArgumentNotNull(template);

        if (getTemplateInvocations(template) > templateRecursiveInvocationLimit) {
            throw new MustacheException(
                    MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED,
                    "Recursive invocation limit exceeded [limit: %s, stack: %s]",
                    templateRecursiveInvocationLimit, templateInvocationStack);
        }
        templateInvocationStack.addFirst(template);
    }

    private int getTemplateInvocations(Template template) {
        int invocations = 0;
        for (Template invocation : templateInvocationStack) {
            if (invocation.equals(template)) {
                invocations++;
            }
        }
        return invocations;
    }

}
