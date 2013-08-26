package org.trimou.engine.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.AbstractEngineTest;

/**
 *
 * @author Martin Kouba
 */
public class IndexResolverTest extends AbstractEngineTest {

    @Test
    public void testNotAnIndex() {

        IndexResolver indexResolver = new IndexResolver(0) {
            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                return null;
            }
        };
        assertTrue(indexResolver.notAnIndex("-1"));
        assertTrue(indexResolver.notAnIndex("size"));
        assertTrue(indexResolver.notAnIndex(".1"));
        assertTrue(indexResolver.notAnIndex("1,5"));
        assertFalse(indexResolver.notAnIndex("1500"));
    }

}
