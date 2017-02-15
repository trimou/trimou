package org.trimou.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;

/**
 *
 * @author Martin Kouba
 *
 */
public class DefaultExecutionContextTest extends AbstractEngineTest {

    @Test
    public void testGlobalExecutionContext() {
        ExecutionContext ctx01 = ExecutionContexts
                .newGlobalExecutionContext(engine.getConfiguration());
        assertNotNull(ctx01);
        assertNull(ctx01.getParent());
        assertNull(ctx01.getFirstContextObject());
        assertNull(ctx01.getValue("this").get());
        ExecutionContext ctx02 = ctx01.setContextObject(new Hammer());
        assertNotNull(ctx02.getParent());
        assertEquals(ctx01, ctx02.getParent());
        assertNotNull(ctx02.getValue("this").get());
        ExecutionContext ctx03 = ctx02
                .setDefiningSections(new ArrayList<>());
        assertNotNull(ctx03.getFirstContextObject());
        assertNotNull(ctx03.getParent());
        assertEquals(ctx02.getFirstContextObject(),
                ctx03.getFirstContextObject());
        assertNull(ctx03.getDefiningSection("foo"));
    }

}
