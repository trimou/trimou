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
        assertEquals(10, literalSupport.getLiteral("+10", null));
        assertEquals(-2010, literalSupport.getLiteral("-2010", null));
        assertEquals("foo", literalSupport.getLiteral("'foo'", null));
        assertEquals("bar.me", literalSupport.getLiteral("\"bar.me\"", null));
        assertEquals(10L, literalSupport.getLiteral("10L", null));
        assertEquals(-2010L, literalSupport.getLiteral("-2010l", null));
        assertNull(literalSupport.getLiteral("2.0", null));
        assertEquals(Boolean.TRUE, literalSupport.getLiteral("true", null));
        assertEquals(Boolean.FALSE, literalSupport.getLiteral("false", null));
    }

}
