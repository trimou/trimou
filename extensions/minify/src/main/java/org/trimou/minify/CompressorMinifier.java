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

import java.io.Reader;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.exception.MustacheException;
import org.trimou.util.IOUtils;

import com.googlecode.htmlcompressor.compressor.Compressor;

/**
 * Minifier backed by <b>com.googlecode.htmlcompressor</b> library.
 *
 * @author Martin Kouba
 *
 * @param <T>
 */
public abstract class CompressorMinifier<T extends Compressor> extends
        AbstractMinifier {

    private static final Logger logger = LoggerFactory
            .getLogger(CompressorMinifier.class);

    T compressor;

    /**
     *
     * @param compressor
     */
    public CompressorMinifier(T compressor) {
        this.compressor = compressor;
    }

    @Override
    public void init(Configuration configuration) {
        initCompressor(compressor, configuration);
    }

    @Override
    public Reader minify(String mustacheName, Reader mustacheContents) {
        if (!match(mustacheName)) {
            return mustacheContents;
        }
        try {
            String source = IOUtils.toString(mustacheContents);
            String compressed = compressor.compress(source);
            logger.debug("Compression finished [saving: {} bytes]",
                    source.length() - compressed.length());
            return new StringReader(compressed);
        } catch (Exception e) {
            throw new MustacheException(
                    "Unable to compress the template contents", e);
        }
    }

    @Override
    public String minify(String text) {
        String compressed = compressor.compress(text);
        logger.debug("Compression finished [saving: {} bytes]", text.length()
                - compressed.length());
        return compressed;
    }

    /**
     * Useful to filter out specific templates, e.g. to only minify files with
     * *.html suffix.
     *
     * @param mustacheName
     * @return <code>true</code> if the minifier should be applied to the given
     *         mustache name, <code>false</code> otherwise
     */
    protected boolean match(String mustacheName) {
        return true;
    }

    /**
     * Initialize the compressor instance.
     *
     * @param compressor
     * @param configuration
     */
    protected void initCompressor(T compressor, Configuration configuration) {
        // No-op
    }

}
