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
package org.trimou.engine.resolver.i18n;

import static org.trimou.engine.priority.Priorities.rightAfter;

import java.util.ResourceBundle;

import org.trimou.engine.resolver.ArrayIndexResolver;
import org.trimou.engine.resolver.DummyTransformResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.lambda.i18n.ResourceBundleLambda;

/**
 * {@link ResourceBundle} resolver. Unlike {@link ResourceBundleLambda} this
 * resolver is not limited to String-based values. However keep in mind that
 * resource bundle keys cannot contain dots.
 *
 * @author Martin Kouba
 * @see ResourceBundle
 * @see ResourceBundleLambda
 */
public class ResourceBundleResolver extends DummyTransformResolver {

    /**
     *
     * @param baseName
     *            The base name of the resource bundle
     */
    public ResourceBundleResolver(String baseName) {
        this(baseName, rightAfter(ArrayIndexResolver.ARRAY_RESOLVER_PRIORITY));
    }

    /**
     *
     * @param baseName
     *            The base name of the resource bundle
     * @param priority
     */
    public ResourceBundleResolver(String baseName, int priority) {
        super(priority, baseName);
    }

    @Override
    public Object transform(Object contextObject, String name,
            ResolutionContext context) {
        ResourceBundle bundle = ResourceBundle.getBundle(matchingName(0),
                getCurrentLocale());
        if (bundle.containsKey(name)) {
            return bundle.getObject(name);
        }
        return null;
    }

}
