package org.trimou.engine.segment;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Iterators;

/**
 *
 * @author Martin Kouba
 */
public class HelperExecutionHandlerTest {

    @Test
    public void testSplitHelperName() {
        assertHelperNameParts("name foo", "name", "foo");
        assertHelperNameParts("name  hash1=\"DD-MM-yyyy HH:mm\"", "name",
                "hash1=\"DD-MM-yyyy HH:mm\"");
    }

    private void assertHelperNameParts(String name, String... parts) {
        assertTrue(Iterators.elementsEqual(
                HelperExecutionHandler.splitHelperName(name),
                Iterators.forArray(parts)));
    }

}
