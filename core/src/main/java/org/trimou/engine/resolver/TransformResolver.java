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
package org.trimou.engine.resolver;

import org.trimou.engine.resolver.i18n.LocaleAwareResolver;

/**
 * This resolver performs some kind of transformation when matched (e.g. formats
 * the given context object based on the given name).
 *
 * The transformation is performed by a separate {@link Transformer} instance or
 * by the resolver itself.
 *
 * @author Martin Kouba
 */
public class TransformResolver extends LocaleAwareResolver implements
        Transformer {

    private String[] matchingNames;

    private final Transformer transformer;

    /**
     *
     * @param priority
     * @param matchNames
     */
    public TransformResolver(int priority, String... matchNames) {
        this(priority, null, matchNames);
    }

    /**
     *
     *
     * @param priority
     * @param transformer
     * @param matchingNames
     */
    public TransformResolver(int priority, Transformer transformer,
            String... matchingNames) {
        super(priority);
        if(matchingNames.length > 0) {
            this.matchingNames = matchingNames;
        }
        this.transformer = transformer;
    }

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (matches(contextObject, name)) {
            return performTransformation(contextObject, name, context);
        }
        return null;
    }

    public Object transform(Object contextObject, String name,
            ResolutionContext context) {
        // Do nothing
        return null;
    }

    /**
     *
     * @param contextObject
     * @param name
     * @return <code>true</code> in case of the resolver matches given context
     *         object and name, <code>false</code> otherwise
     */
    protected boolean matches(Object contextObject, String name) {
        return contextObject != null && matches(name);
    }

    /**
     *
     * @param contextObject
     * @param name
     * @param context
     * @return the result of the transformation or <code>null</code> if the
     *         transformation was not successfull
     */
    protected Object performTransformation(Object contextObject, String name,
            ResolutionContext context) {
        return transformer != null ? transformer.transform(contextObject, name,
                context) : transform(contextObject, name, context);
    }

    protected boolean matches(String name) {
        for (String matchingName : matchingNames) {
            if (matchingName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected String matchingName(int index) {
        return matchingNames[index];
    }

    protected void setMatchingNames(String... matchNames) {
        if(this.matchingNames != null) {
            throw new IllegalStateException("Matching names already set!");
        }
        this.matchingNames = matchNames;
    }

}
