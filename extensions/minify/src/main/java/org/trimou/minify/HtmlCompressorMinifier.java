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
package org.trimou.minify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.trimou.engine.config.Configuration;
import org.trimou.util.Patterns;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

/**
 *
 * @author Martin Kouba
 */
public class HtmlCompressorMinifier extends CompressorMinifier<HtmlCompressor> {

    public HtmlCompressorMinifier() {
        super(new HtmlCompressor());
    }

    @Override
    public void init(Configuration configuration) {
        super.init(configuration);

        List<Pattern> preservePatterns = compressor.getPreservePatterns();

        // Note that we only consider the configuration-based delimiters so that
        // "set delimiters" tag might not work properly
        Pattern mustachePattern = Patterns.newMustacheTagPattern(configuration);

        if (preservePatterns == null) {
            compressor.setPreservePatterns(Collections
                    .singletonList(mustachePattern));
        } else {
            List<Pattern> newPatterns = new ArrayList<Pattern>(preservePatterns);
            newPatterns.add(mustachePattern);
            compressor.setPreservePatterns(newPatterns);
        }
    }

}
