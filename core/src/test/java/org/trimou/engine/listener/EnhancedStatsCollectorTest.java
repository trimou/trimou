package org.trimou.engine.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.listener.AbstractStatsCollector.Stats;
import org.trimou.engine.listener.EnhancedStatsCollector.ExecutionData;
import org.trimou.lambda.InputLiteralLambda;
import org.trimou.lambda.Lambda;

import com.google.common.base.Predicates;

/**
 *
 * @author Martin Kouba
 */
public class EnhancedStatsCollectorTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
    }

    @Test
    public void testDataCollecting() {

        final int sleepConstant = 10;
        final AtomicInteger counter = new AtomicInteger();
        EnhancedStatsCollector collector = new EnhancedStatsCollector();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build();
        Lambda sleeper = new InputLiteralLambda() {
            final Random random = new Random();

            @Override
            public boolean isReturnValueInterpolated() {
                return false;
            }

            @Override
            public String invoke(String text) {
                try {
                    int count = counter.incrementAndGet();
                    // The first execution results in error
                    if(count == 1) {
                        throw new RuntimeException();
                    }
                    Thread.sleep(sleepConstant + random.nextInt(sleepConstant));
                } catch (InterruptedException e) {
                    throw new IllegalStateException();
                }
                return text;
            }
        };

        Mustache mustache = engine.compileMustache("foo", "{{this}}");
        int loop = 50;
        for (int i = 0; i < loop; i++) {
            try {
                mustache.render(sleeper);
            } catch (Exception e) {
                // Expected
            }
        }

        Stats stats = collector.getStats(mustache);
        assertNotNull(stats);
        assertEquals(loop - 1, stats.getFinished());
        assertEquals(1l, stats.getErrors());
        assertTrue(stats.getMaxTime() > 0);
        assertTrue(stats.getMinTime() > 0);
        assertTrue(stats.getMeanTime() > 0 && stats.getMeanTime() <= stats.getMaxTime());
        assertEquals(1, collector.getStats().size());

        Collection<ExecutionData> rawData = collector.getRawData(mustache);
        assertEquals(loop, rawData.size());
    }

    @Test
    public void testClearData() {
        EnhancedStatsCollector collector = new EnhancedStatsCollector();
        Mustache mustache = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build()
                .compileMustache("bar", "BAR");
        mustache.render(null);
        assertEquals(1, collector.getStats(mustache).getFinished());
        collector.clearData();
        assertNull(collector.getStats(mustache));
    }

    @Test
    public void testCustomPredicate() {
        EnhancedStatsCollector collector = new EnhancedStatsCollector(Predicates.<String>alwaysFalse(), TimeUnit.DAYS);
        Mustache mustache = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build()
                .compileMustache("qux", "Oops");
        mustache.render(null);
        assertNull(collector.getStats(mustache));
    }

}
