package org.trimou.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class IteratorsTest {

    @Test
    public void testSingletonIterator() {
        Iterator<String> iterator = Iterables.singletonIterator("foo");
        assertTrue(iterator.hasNext());
        assertEquals("foo", iterator.next());
        assertFalse(iterator.hasNext());
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException expected) {
        }
    }

}
