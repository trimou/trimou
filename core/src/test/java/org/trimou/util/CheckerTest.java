package org.trimou.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class CheckerTest {

    @Test
    public void testIsFalsy() {
        assertTrue(Checker.isFalsy(null));
        assertTrue(Checker.isFalsy(false));
        assertTrue(Checker.isFalsy(new String[] {}));
        assertTrue(Checker.isFalsy(Collections.emptyList()));
        assertTrue(Checker.isFalsy(""));
        assertTrue(Checker.isFalsy(new StringBuilder()));
        assertTrue(Checker.isFalsy(0));
        assertTrue(Checker.isFalsy(BigDecimal.ZERO));
        assertTrue(Checker.isFalsy(new BigDecimal("0.000")));
        assertFalse(Checker.isFalsy(Boolean.TRUE));
        assertFalse(Checker.isFalsy(new Object[] { "foo" }));
        assertFalse(Checker.isFalsy(Collections.singleton("foo")));
        assertFalse(Checker.isFalsy("foo"));
        assertFalse(Checker.isFalsy(new StringBuilder().append("foo")));
        assertFalse(Checker.isFalsy(-10));
        assertFalse(Checker.isFalsy(BigDecimal.TEN));
        assertFalse(Checker.isFalsy(0.2));
        assertFalse(Checker.isFalsy(new BigDecimal("-0.01")));
    }

}
