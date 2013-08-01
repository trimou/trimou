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
import java.io.StringWriter;
import java.io.Writer;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static String toString(final Reader input) throws IOException {
        return toString(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     *
     * @param input
     * @param bufferSize
     * @return
     * @throws IOException
     */
    public static String toString(final Reader input, final int bufferSize)
            throws IOException {
        final StringWriter writer = new StringWriter();
        copy(input, writer, bufferSize);
        return writer.toString();
    }

    /**
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
