/*
 * Copyright 2017 Trimou team
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
package org.trimou.cdi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class CDIBeanResolverTest extends WeldSETest {

    @Test
    public void testInterpolation() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        assertEquals("foo", engine.compileMustache("cdi_bean_resolver_weld_se", "{{appScopedBean.name}}").render(null));
    }

}
