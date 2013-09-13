package org.trimou.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Iterators;

/**
 *
 * @author Martin Kouba
 */
public class DotKeySplitterTest {

    @Test
    public void testSplit() {
        DotKeySplitter splitter = new DotKeySplitter();
        assertEquals(3, Iterators.size(splitter.split("a..bar:.c")));
        assertTrue(Iterators.contains(splitter.split("a..bar:.c"), "a"));
        assertTrue(Iterators.contains(splitter.split("a..bar:.c"), "bar:"));
        assertTrue(Iterators.contains(splitter.split("a..bar:.c"), "c"));
        assertEquals(1, Iterators.size(splitter.split(". ")));
        assertEquals(1, Iterators.size(splitter.split("foo")));
    }

}
