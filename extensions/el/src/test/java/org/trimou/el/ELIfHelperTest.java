package org.trimou.el;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineFactory;

/**
 * @author Martin Kouba
 */
public class ELIfHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        MustacheEngine engine = MustacheEngineFactory.defaultEngine();
        assertEquals(
                "no", engine
                        .compileMustache("elifhelper_01",
                                "{{#if 'this gt 10' else='no'}}yes{{/if}}")
                        .render(1));
    }
}
