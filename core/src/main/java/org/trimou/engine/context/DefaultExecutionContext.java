/*
 * Copyright 2015 Martin Kouba
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.parser.Template;
import org.trimou.engine.resolver.EnhancedResolver;
import org.trimou.engine.resolver.EnhancedResolver.Hint;
import org.trimou.engine.resolver.Placeholder;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.segment.Segment;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * A default implementation.
 *
 * @author Martin Kouba
 */
final class DefaultExecutionContext implements ExecutionContext {

    private final DefaultExecutionContext parent;

    private final Configuration configuration;

    protected final Object contextObject;

    protected final Template templateInvocation;

    protected final int invocationLimitCounter;

    protected final Map<String, Segment> definingSections;

    protected final Resolver[] resolvers;

    /**
     *
     * @param parent
     * @param configuration
     * @param contextObject
     * @param templateInvocation
     * @param templateInvocations
     * @param invocationLimitCounter
     * @param definingSections
     * @param resolvers
     */
    DefaultExecutionContext(DefaultExecutionContext parent,
            Configuration configuration, Object contextObject,
            Template templateInvocation, int invocationLimitCounter,
            Map<String, Segment> definingSections, Resolver[] resolvers) {
        this.parent = parent;
        this.configuration = configuration;
        this.contextObject = contextObject;
        this.templateInvocation = templateInvocation;
        this.invocationLimitCounter = invocationLimitCounter;
        this.definingSections = definingSections;
        this.resolvers = resolvers;
    }

    @Override
    public ValueWrapper getValue(String key, String[] keyParts,
            AtomicReference<Hint> hintRef) {

        ValueWrapper value = new ValueWrapper(key);
        Object lastValue = null;

        if (keyParts == null || keyParts.length == 0) {
            Iterator<String> parts = configuration.getKeySplitter().split(key);
            lastValue = resolveLeadingContextObject(parts.next(), value,
                    hintRef);
            if (lastValue == null) {
                // Leading context object not found - miss
                return value;
            }
            while (parts.hasNext()) {
                value.processNextPart();
                lastValue = resolve(lastValue, parts.next(), value, false);
                if (lastValue == null) {
                    // Not found - miss
                    return value;
                }
            }
        } else {
            lastValue = resolveLeadingContextObject(keyParts[0], value, hintRef);
            if (lastValue == null) {
                // Leading context object not found - miss
                return value;
            }
            if (keyParts.length > 1) {
                for (int i = 1; i < keyParts.length; i++) {
                    value.processNextPart();
                    lastValue = resolve(lastValue, keyParts[i], value, false);
                    if (lastValue == null) {
                        // Not found - miss
                        return value;
                    }
                }
            }
        }

        if (!Placeholder.NULL.equals(lastValue)) {
            value.set(lastValue);
        }
        return value;
    }

    @Override
    public ValueWrapper getValue(String key) {
        return getValue(key, null, null);
    }

    @Override
    public ExecutionContext setContextObject(Object object) {
        return new DefaultExecutionContext(this, configuration, object, null,
                invocationLimitCounter, null, resolvers);
    }

    @Override
    public Object getFirstContextObject() {
        if (contextObject != null) {
            return contextObject;
        }
        if (parent != null) {
            return parent.getFirstContextObject();
        }
        return null;
    }

    @Override
    public ExecutionContext setTemplateInvocation(Template template) {
        if (invocationLimitCounter < 0
                && getTemplateInvocations(template) > configuration
                        .getIntegerPropertyValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT)) {
            throw new MustacheException(
                    MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED,
                    "Recursive invocation limit exceeded [limit: %s, level: %s, template: %s]",
                    configuration
                            .getIntegerPropertyValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT),
                    invocationLimitCounter, templateInvocation);
        }
        return new DefaultExecutionContext(this, configuration, null, template,
                invocationLimitCounter - 1, null, resolvers);
    }

    @Override
    public ExecutionContext setDefiningSections(Iterable<Segment> segments) {
        Map<String, Segment> definingSections = null;
        for (Segment segment : segments) {
            if (getDefiningSection(segment.getText()) == null) {
                if (definingSections == null) {
                    definingSections = new HashMap<String, Segment>();
                }
                definingSections.put(segment.getText(), segment);
            }
        }
        return new DefaultExecutionContext(this, configuration, null, null,
                invocationLimitCounter, definingSections, resolvers);
    }

    @Override
    public Segment getDefiningSection(String name) {
        Segment section = null;
        if (definingSections != null) {
            section = definingSections.get(name);
        }
        if (section == null && parent != null) {
            section = parent.getDefiningSection(name);
        }
        return section;
    }

    @Override
    public ExecutionContext getParent() {
        return parent;
    }

    private int getTemplateInvocations(Template template) {
        int invocations = 0;
        if (templateInvocation != null && templateInvocation.equals(template)) {
            invocations++;
        }
        if (parent != null) {
            invocations += parent.getTemplateInvocations(template);
        }
        return invocations;
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
    private Object resolveLeadingContextObject(String name, ValueWrapper value,
            AtomicReference<Hint> hintRef) {

        Object leading = resolveContextObject(name, value, hintRef);

        if (leading == null) {
            // Leading context object not found - try to resolve context
            // unrelated objects (JNDI lookup, CDI, etc.)
            Hint hint = hintRef != null ? hintRef.get() : null;
            if (hint != null) {
                leading = hint.resolve(null, name, value);
            }
            if (leading == null) {
                leading = resolve(null, name, value, hint == null
                        && hintRef != null);
            }
        }
        return leading;
    }

    private Object resolveContextObject(String name, ValueWrapper value,
            AtomicReference<Hint> hintRef) {

        Object leading = null;

        if (contextObject != null) {
            Hint hint = hintRef != null ? hintRef.get() : null;
            if (hint != null) {
                leading = hint.resolve(contextObject, name, value);
            }
            if (leading == null) {
                leading = resolve(contextObject, name, value, hint == null
                        && hintRef != null);
            }
        }
        if (leading == null && parent != null) {
            leading = parent.resolveContextObject(name, value, hintRef);
        }
        return leading;
    }

    private Object resolve(Object contextObject, String name,
            ValueWrapper value, boolean createHint) {
        Object resolved = null;
        for (final Resolver resolver1 : resolvers) {
            resolved = resolver1.resolve(contextObject, name, value);
            if (resolved != null) {
                if (createHint) {
                    // Initialize a new hint if possible
                    Resolver resolver = resolver1;
                    if (resolver instanceof EnhancedResolver) {
                        value.setHint(((EnhancedResolver) resolver).createHint(
                                contextObject, name, value));
                    }
                }
                break;
            }
        }
        return resolved;
    }

}
