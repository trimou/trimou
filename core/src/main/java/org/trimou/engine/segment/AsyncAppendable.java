/*
 * Copyright 2015 Martin Kouba
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
package org.trimou.engine.segment;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.Options;

/**
 * A wrapper for an asynchronous appendable. This construct is not thread-safe.
 *
 * @author Martin Kouba
 * @see Options#executeAsync(org.trimou.handlebars.Options.HelperExecutable)
 */
class AsyncAppendable implements Appendable {

    protected final Appendable parent;

    protected final StringBuilder buffer;

    protected volatile Future<AsyncAppendable> future;

    /**
     *
     * @param parent
     */
    AsyncAppendable(Appendable parent) {
        this.parent = parent;
        this.buffer = new StringBuilder();
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        buffer.append(csq);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end)
            throws IOException {
        buffer.append(csq, start, end);
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        buffer.append(c);
        return this;
    }

    /**
     * Append the result to the parent.
     */
    private void flush() {
        try {
            parent.append(future.get(60, TimeUnit.SECONDS).collect(this));
            parent.append(buffer);
            if (parent instanceof AsyncAppendable) {
                ((AsyncAppendable) parent).flush();
            }
        } catch (Exception e) {
            throw new MustacheException(
                    MustacheProblem.RENDER_ASYNC_PROCESSING_ERROR, e);
        }
    }

    /**
     *
     * @param latch
     *            The top-level async appendable
     * @return the collected output
     */
    private CharSequence collect(AsyncAppendable latch) {

        if (parent.equals(latch) && future == null) {
            return buffer;
        }

        StringBuilder ret = null;

        if (parent instanceof AsyncAppendable && !parent.equals(latch)) {
            ret = new StringBuilder();
            ret.append(((AsyncAppendable) parent).collect(latch));
        }

        if (future != null) {
            try {
                if (ret == null) {
                    ret = new StringBuilder();
                }
                AsyncAppendable result = future.get(60, TimeUnit.SECONDS);
                if (result.future != null) {
                    ret.append(result.collect(this));
                } else {
                    ret.append(result.buffer);
                    ret.append(buffer);
                }
                return ret;
            } catch (Exception e) {
                throw new MustacheException(
                        MustacheProblem.RENDER_ASYNC_PROCESSING_ERROR, e);
            }
        } else {
            return ret != null ? ret.append(buffer) : buffer;
        }
    }

    void setFuture(Future<AsyncAppendable> future) {
        this.future = future;
    }

    static void flushIfNeeded(Appendable appendable) {
        if (appendable instanceof AsyncAppendable) {
            ((AsyncAppendable) appendable).flush();
        }
    }

}
