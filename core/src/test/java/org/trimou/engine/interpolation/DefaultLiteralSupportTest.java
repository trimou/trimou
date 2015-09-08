package org.trimou.engine.interpolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class DefaultLiteralSupportTest {

    @Test
    public void testGetLiteral() {
        LiteralSupport literalSupport = new DefaultLiteralSupport();
        assertNull(literalSupport.getLiteral("foo", null));
        assertNull(literalSupport.getLiteral("1.0", null));
        assertEquals(Integer.valueOf(10),
                literalSupport.getLiteral("+10", null));
        assertEquals(Integer.valueOf(-2010),
                literalSupport.getLiteral("-2010", null));
        assertEquals("foo", literalSupport.getLiteral("'foo'", null));
        assertEquals("bar.me", literalSupport.getLiteral("\"bar.me\"", null));
        assertEquals(Long.valueOf(10), literalSupport.getLiteral("10L", null));
        assertEquals(Long.valueOf(-2010),
                literalSupport.getLiteral("-2010l", null));
        assertNull(literalSupport.getLiteral("2.0", null));
    }

}
