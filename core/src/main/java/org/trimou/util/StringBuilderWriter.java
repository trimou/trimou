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
package org.trimou.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.trimou.annotations.Internal;

/**
 * Makes use of {@link StringBuilder} instead of {@link StringBuffer} (unlike
 * {@link StringWriter}).
 *
 * @author Martin Kouba
 */
@Internal
public class StringBuilderWriter extends Writer {

    private final StringBuilder builder;

    /**
     *
     * @param builder
     */
    public StringBuilderWriter(StringBuilder builder) {
        this.builder = builder;
    }

    /**
     *
     */
    public StringBuilderWriter() {
        builder = new StringBuilder();
    }

    /**
     *
     * @param capacity
     */
    public StringBuilderWriter(int capacity) {
        this.builder = new StringBuilder(capacity);
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) {
        builder.append(cbuf, off, len);
    }

    @Override
    public void write(final int c) {
        builder.append(c);
    }

    @Override
    public void write(final char[] cbuf) {
        builder.append(cbuf);
    }

    @Override
    public void write(final String str) throws IOException {
        if (str != null) {
            builder.append(str);
        }
    }

    @Override
    public void write(final String str, final int off, final int len)
            throws IOException {
        if (str != null) {
            builder.append(str, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}