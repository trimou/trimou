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
package org.trimou.engine.id;

import java.util.concurrent.atomic.AtomicLong;

import org.trimou.engine.config.AbstractConfigurationAware;

/**
 * A default {@link IdentifierGenerator} using a global sequence backed by an
 * {@link AtomicLong}.
 *
 * @author Martin Kouba
 */
public class SequenceIdentifierGenerator extends AbstractConfigurationAware
        implements IdentifierGenerator {

    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public long generate(Class<? extends Identified> componentType) {
        return sequence.incrementAndGet();
    }

}
