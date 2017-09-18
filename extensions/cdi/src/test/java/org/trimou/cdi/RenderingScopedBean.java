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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.trimou.cdi.context.RenderingScoped;

@RenderingScoped
@Named("renderingScopedBean")
public class RenderingScopedBean {

    static final AtomicBoolean DESTROYED = new AtomicBoolean(false);

    public String getName() {
        return "bar";
    }

    public String getId() {
        return UUID.randomUUID().toString();
    }

    @PreDestroy
    void dispose() {
        DESTROYED.set(true);
    }

}
