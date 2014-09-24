package org.trimou.jdk8.cache;

import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.cache.ComputingCacheConcurrencyTest;
import org.trimou.jdk8.cache.ConcurrentHashMapComputingCacheFactory;

/**
 *
 * @author Martin Kouba
 */
public class ConcurrentHashMapComputingCacheConcurrencyTest extends
        ComputingCacheConcurrencyTest {

    @Override
    public void buildEngine() {
        engine = MustacheEngineBuilder.newBuilder().setComputingCacheFactory(
                new ConcurrentHashMapComputingCacheFactory()).build();
    }

}
