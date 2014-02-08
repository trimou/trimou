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
import org.trimou.engine.listener.SimpleStatisticsCollector.SimpleTemplateStatistics;
import org.trimou.lambda.InputLiteralLambda;
import org.trimou.lambda.Lambda;

import com.google.common.base.Predicates;

/**
 *
 * @author Martin Kouba
 */
public class SimpleStatisticsCollectorTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {
    }

    @Test
    public void testDataCollecting() {

        SimpleStatisticsCollector collector = new SimpleStatisticsCollector();
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

        assertNull(collector.getSimpleStatistics("unknown"));
        SimpleTemplateStatistics statistics = collector
                .getSimpleStatistics("foo");
        assertNotNull(statistics);
        assertEquals(loop, statistics.getExecutions());
        System.out.println(statistics);
        assertEquals(1, collector.getSimpleStatistics().size());

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
        SimpleStatisticsCollector collector = new SimpleStatisticsCollector();
        Mustache mustache = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build()
                .compileMustache("bar", "BAR");
        mustache.render(null);
        assertEquals(1, collector.getSimpleStatistics("bar").getExecutions());
        collector.clearData();
        assertNull(collector.getSimpleStatistics("bar"));
    }

    @Test
    public void testCustomMatcher() {
        SimpleStatisticsCollector collector = new SimpleStatisticsCollector(Predicates.<String>alwaysFalse(), TimeUnit.DAYS);
        Mustache mustache = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(collector).build()
                .compileMustache("qux", "Oops");
        mustache.render(null);
        assertNull(collector.getSimpleStatistics("qux"));
    }

}
