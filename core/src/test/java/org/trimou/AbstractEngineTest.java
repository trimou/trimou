package org.trimou;

import org.junit.Before;
import org.trimou.engine.MustacheEngine;

/**
 *
 * @author Martin Kouba
 */
public abstract class AbstractEngineTest extends AbstractTest {

    protected MustacheEngine engine;

    @Before
    public void buildEngine() {
        engine = MustacheEngine.builder().build();
    }

}
