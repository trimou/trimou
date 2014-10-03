package org.trimou.jdk8.cache;

import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.cache.ComputingCacheTest;

/**
 *
 * @author Martin Kouba
 */
public class MapBackedComputingCacheTest extends
        ComputingCacheTest {

    @Override
    public void buildEngine() {
        engine = MustacheEngineBuilder
                .newBuilder()
                .setComputingCacheFactory(
                        new MapBackedComputingCacheFactory()).build();
    }

}
