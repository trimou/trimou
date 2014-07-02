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
import java.io.Reader;
import java.io.Writer;

import org.trimou.annotations.Internal;

import com.google.common.io.CharStreams;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class IOUtils {

    private IOUtils() {
    }

    /**
     * The reader is closed right after the input is read.
     *
     * @param input
     * @return the contents of a reader as a string
     * @throws IOException
     */
    public static String toString(final Reader input) throws IOException {
        Checker.checkArgumentNotNull(input);
        try {
            return CharStreams.toString(input);
        } finally {
            // Input cannot be null
            input.close();
        }
    }

    /**
     * Does not close the {@code Reader}.
     *
     * @param input
     * @param bufferSize
     * @return the contents of a reader as a string
     * @throws IOException
     */
    public static String toString(final Reader input, final int bufferSize)
            throws IOException {
        final StringBuilderWriter writer = new StringBuilderWriter();
        copy(input, writer, bufferSize);
        return writer.toString();
    }

    /**
     * Does not close the {@code Reader}.
     *
     * @param input
     * @param output
     * @param bufferSize
     * @throws IOException
     */
    public static void copy(final Reader input, final Writer output,
            final int bufferSize) throws IOException {
        final char[] buffer = new char[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }

}
