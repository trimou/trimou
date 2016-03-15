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
package org.trimou.engine.text;

import java.io.IOException;

import org.trimou.engine.config.AbstractConfigurationAware;
import org.trimou.util.Checker;
import org.trimou.util.Escaper;

/**
 *
 * @author Martin Kouba
 */
public class DefaultTextSupport extends AbstractConfigurationAware
        implements TextSupport {

    private final Escaper escaper;

    /**
     *
     */
    public DefaultTextSupport() {
        this(Escaper.builder().add('"', "&quot;").add('\'', "&#39;")
                .add('&', "&amp;").add('<', "&lt;").add('>', "&gt;").build());
    }

    /**
     *
     * @param escaper
     * @see Escaper#builder()
     */
    public DefaultTextSupport(Escaper escaper) {
        Checker.checkArgumentNotNull(escaper);
        this.escaper = escaper;
    }

    @Override
    public String escapeHtml(String input) {
        return escaper.escape(input);
    }

    @Override
    public void appendEscapedHtml(String input, Appendable appendable)
            throws IOException {
        escaper.escape(input, appendable);
    }

}
