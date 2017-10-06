package org.trimou.el;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class ELEachHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        assertEquals("123",
                engine.compileMustache("eleachhelper_01", "{{#each '[1,2,3]'}}{{this}}{{/each}}").render(1));
    }

}
