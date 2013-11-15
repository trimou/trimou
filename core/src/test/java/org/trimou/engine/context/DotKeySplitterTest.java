package org.trimou.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;
import org.trimou.engine.interpolation.DotKeySplitter;

/**
 *
 * @author Martin Kouba
 */
public class DotKeySplitterTest {

    @Test
    public void testSplit() {
        DotKeySplitter splitter = new DotKeySplitter();
        assertIterator(splitter.split("a..bar:.c"), "a", "bar:", "c");
        assertIterator(splitter.split(". "), " ");
        assertIterator(splitter.split("."), ".");
        assertIterator(splitter.split("foo"), "foo");
    }

    private void assertIterator(Iterator<String> iterator, Object... elements) {
        int idx = 0;
        while (iterator.hasNext()) {
            assertEquals(elements[idx], iterator.next());
            idx++;
        }
        if(idx != elements.length) {
            fail("Incorrect number of elements");
        }
    }

}
