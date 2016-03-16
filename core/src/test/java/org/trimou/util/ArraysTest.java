package org.trimou.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class ArraysTest {

    @Test
    public void testContains() {
        assertTrue(Arrays.contains(new Character[] { 'v', 'o', 'j', 't', 'a' },
                'a'));
        assertFalse(
                Arrays.contains(new Character[] { 'j', 'a', 's', 'a' }, null));
        assertTrue(Arrays.contains(new Character[] { 'l', null }, null));
    }

}
