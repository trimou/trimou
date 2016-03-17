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
import java.nio.CharBuffer;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public final class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private IOUtils() {
    }

    /**
     * The reader is closed right after the input is read.
     *
     * @param input
     * @return the contents of a reader as a string
     * @throws IOException
     */
    public static String toString(Reader input) throws IOException {
        return toString(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * The reader is closed right after the input is read.
     *
     * @param input
     * @param bufferSize
     * @return the contents of a reader as a string
     * @throws IOException
     */
    public static String toString(Reader input, int bufferSize)
            throws IOException {
        return toString(input, bufferSize, true);
    }

    /**
     * Does not close the {@code Reader}.
     *
     * @param input
     * @param bufferSize
     * @param close
     * @return the contents of a reader as a string
     * @throws IOException
     */
    public static String toString(Reader input, int bufferSize, boolean close)
            throws IOException {
        Checker.checkArgumentNotNull(input);
        StringBuilder builder = new StringBuilder();
        try {
            copy(input, builder, bufferSize);
        } finally {
            if (close) {
                input.close();
            }
        }
        return builder.toString();
    }

    /**
     *
     * @param in
     * @param out
     * @param bufferSize
     * @throws IOException
     */
    public static void copy(Readable in, Appendable out, int bufferSize)
            throws IOException {
        CharBuffer buffer = CharBuffer.allocate(bufferSize);
        while (in.read(buffer) != -1) {
            buffer.flip();
            out.append(buffer);
            buffer.clear();
        }
    }

}
