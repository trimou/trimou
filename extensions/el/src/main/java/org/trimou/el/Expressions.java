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
package org.trimou.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELProcessor;
import javax.el.ELResolver;

import org.trimou.engine.config.Configuration;
import org.trimou.handlebars.Options;

/**
 *
 * @author Martin Kouba
 */
class Expressions {

    private Expressions() {
    }

    /**
     * Note that we have to use a separate {@link ELProcessor} for each evaluation
     * as the {@link OptionsELResolver} may not be reused.
     *
     * @param expression
     * @param options
     * @return the result of the expression evaluation
     */
    static Object eval(String expression, Options options, Configuration configuration) {
        ELProcessorFactory elpFactory = (ELProcessorFactory) configuration
                .getPropertyValue(ELProcessorFactory.EL_PROCESSOR_FACTORY_KEY);
        ELProcessor elp = elpFactory.createELProcessor(configuration);
        elp.getELManager().addELResolver(new OptionsELResolver(options));
        return elp.eval(expression);
    }

    static class OptionsELResolver extends ELResolver {

        private final Options options;

        OptionsELResolver(Options options) {
            this.options = options;
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            if (context == null) {
                throw new NullPointerException();
            }
            if (base == null) {
                Object value = options.getValue(property.toString());
                if (value != null) {
                    context.setPropertyResolved(true);
                    return value;
                }
            }
            return null;
        }

        @Override
        public Class<?> getType(ELContext context, Object base, Object property) {
            return null;
        }

        @Override
        public void setValue(ELContext context, Object base, Object property, Object value) {
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property) {
            return false;
        }

        @Override
        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
            return null;
        }

        @Override
        public Class<?> getCommonPropertyType(ELContext context, Object base) {
            return null;
        }

    }

}
