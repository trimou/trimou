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
        assertNull(literalSupport.getLiteral("foo"));
        assertNull(literalSupport.getLiteral("1.0"));
        assertEquals(10, literalSupport.getLiteral("+10"));
        assertEquals(-2010, literalSupport.getLiteral("-2010"));
        assertEquals("foo", literalSupport.getLiteral("'foo'"));
        assertEquals("bar.me", literalSupport.getLiteral("\"bar.me\""));
    }

}
