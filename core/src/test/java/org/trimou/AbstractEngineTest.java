package org.trimou;

import org.junit.Before;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineFactory;

/**
 * @author Martin Kouba
 */
public abstract class AbstractEngineTest extends AbstractTest {

    protected MustacheEngine engine;

    @Before
    public void buildEngine() {
        engine = MustacheEngineFactory.defaultEngine();
    }
}
