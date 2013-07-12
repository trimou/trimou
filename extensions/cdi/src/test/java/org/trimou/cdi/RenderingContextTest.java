package org.trimou.cdi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class RenderingContextTest extends WeldSETest {

    @Test
    public void testInterpolation() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        assertEquals(
                "bar",
                engine.compileMustache("cdi_rendering_context_weld_se",
                        "{{renderingScopedBean.name}}").render(null));
    }

}
