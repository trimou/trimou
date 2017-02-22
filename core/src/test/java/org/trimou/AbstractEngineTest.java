package org.trimou;

import org.junit.Before;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public abstract class AbstractEngineTest extends AbstractTest {

    protected MustacheEngine engine;

    @Before
    public void buildEngine() {
        engine = MustacheEngineBuilder.newBuilder().build();
    }

}
