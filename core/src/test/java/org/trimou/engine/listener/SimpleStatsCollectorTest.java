package org.trimou.engine.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.listener.SimpleStatsCollector.SimpleStats;
import org.trimou.lambda.InputLiteralLambda;
import org.trimou.lambda.Lambda;

/**
 *
 * @author Martin Kouba
 */
public class SimpleStatsCollectorTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
    }

    @Test
    public void testDataCollecting() {

        SimpleStatsCollector collector = new SimpleStatsCollector();
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
                    Thread.sleep(10l + random.nextInt(10));
                } catch (InterruptedException e) {
                    throw new IllegalStateException();
                }
                return text;
            }
        };

        Mustache mustache = engine.compileMustache("foo", "{{this}}");
        int loop = 50;
        for (int i = 0; i < loop; i++) {
            mustache.render(sleeper);
        }

        assertNull(collector.getSimpleStats("unknown"));
        SimpleStats statistics = collector
                .getSimpleStats("foo");
        assertNotNull(statistics);
        assertEquals(loop, statistics.getExecutions());
        System.out.println(statistics);
        assertEquals(1, collector.getSimpleStats().size());

        assertNull(collector.getData("fooooo"));
        Map<Long, Long> data = collector.getData("foo");
        assertNotNull(data);
        long totalExecutions = 0;
        for (Long executions : data.values()) {
            totalExecutions += executions;
        }
        assertEquals(loop, totalExecutions);
        assertEquals(1, collector.getData().size());
    }

    @Test
    public void testClearData() {
        SimpleStatsCollector collector = new SimpleStatsCollector();
        Mustache mustache = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build()
                .compileMustache("bar", "BAR");
        mustache.render(null);
        assertEquals(1, collector.getSimpleStats("bar").getExecutions());
        collector.clearData();
        assertNull(collector.getSimpleStats("bar"));
    }

    @Test
    public void testCustomPredicate() {
        SimpleStatsCollector collector = new SimpleStatsCollector((t) -> false,
                TimeUnit.DAYS);
        Mustache mustache = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build()
                .compileMustache("qux", "Oops");
        mustache.render(null);
        assertNull(collector.getSimpleStats("qux"));
    }

}
